package com.yondu.knowledgebase.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.DTO.review.ReviewDTOMapper;
import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.Review;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.WorkflowStep;
import com.yondu.knowledgebase.entities.WorkflowStepApprover;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.EntityType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.enums.Permission;
import com.yondu.knowledgebase.enums.ReviewStatus;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import com.yondu.knowledgebase.repositories.ReviewRepository;
import com.yondu.knowledgebase.repositories.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PageVersionRepository pageVersionRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserPermissionValidatorService userPermissionValidatorService;
    private final AuditLogService auditLogService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PageVersionRepository pageVersionRepository,
            NotificationService notificationService, UserRepository userRepository,
            UserPermissionValidatorService userPermissionValidatorService, AuditLogService auditLogService) {
        this.reviewRepository = reviewRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.userPermissionValidatorService = userPermissionValidatorService;
        this.auditLogService = auditLogService;
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviews(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAll(pageRequest);
        List<Review> reviews = reviewPages.getContent();

        List<ReviewDTO.BaseResponse> review = reviews.stream()
                .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(review, page, size, (long) review.size());
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviewsByStatus(String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAllByStatus(ReviewStatus.valueOf(status), pageRequest);
        List<Review> reviews = reviewPages.getContent();

        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found");
        }

        List<ReviewDTO.BaseResponse> review = reviews.stream()
                .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                .collect(Collectors.toList());
        return new PaginatedResponse<>(review, page, size, (long) review.size());
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviewsByPageTitle(String title, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAllByPageVersionTitle("%" + title.toLowerCase() + "%",
                pageRequest);
        List<Review> reviews = reviewPages.getContent();

        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found");
        }
        List<ReviewDTO.BaseResponse> review = reviews.stream()
                .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(review, page, size, (long) review.size());
    }

    public ReviewDTO.BaseResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + reviewId + " not found"));

        return ReviewDTOMapper.mapToBaseResponse(review);
    }

    public ReviewDTO.BaseResponse createReview(Long pageId, Long versionId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageVersion pageVersion = pageVersionRepository.findByPageIdAndId(pageId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));

        if (isContentFullyApproved(pageVersion))
            throw new RequestValidationException("Content is already approved and posted in public.");

        if (reviewRepository.existsByPageVersionAndStatus(pageVersion, ReviewStatus.PENDING.getCode())) {
            throw new RequestValidationException("A pending review already exists for this page version.");
        }
        if (isContentDisapproved(pageVersion))
            throw new RequestValidationException("Someone already disapproved the content.");

        boolean pageOwner = currentUser.getId().equals(pageVersion.getPage().getAuthor().getId());
        System.out.println(pageOwner);
        long pId = pageVersion.getPage().getId();

        String requiredPermission = Permission.CREATE_CONTENT.getCode();

        if (!pagePermissionGranted(pId, requiredPermission)) {
            if (!pageOwner)
                throw new RequestValidationException("You are not permitted to submit this page.");
        } else if (!pagePermissionGranted(pId, Permission.UPDATE_CONTENT.getCode())) {
            if (!pageOwner)
                throw new RequestValidationException("You are not permitted to submit this page.");
        }

        Review review = new Review();
        review.setPageVersion(pageVersion);
        review.setUser(null);
        review.setWorkflowStep(null);
        review.setComment("");
        review.setReviewDate(LocalDateTime.now());
        review.setStatus(ReviewStatus.PENDING.getCode());

        reviewRepository.save(review);

        // Notify current step approvers
        Map<WorkflowStep, Boolean> notifyApprovers = isStepDone(pageVersion);
        List<WorkflowStep> sortedSteps = sortSteps(notifyApprovers.keySet());

        for (WorkflowStep step : sortedSteps) {
            boolean isStepDone = notifyApprovers.get(step);

            if (!isStepDone) {
                Set<User> approvers = getStepApprovers(step);

                for (User approver : approvers) {

                    if (!reviewRepository.hasUserApprovedContentInPageVersion(review.getPageVersion(), approver)) {
                        notificationService.createNotification(new NotificationDTO.BaseRequest(approver.getId(),
                                review.getPageVersion().getPage().getAuthor().getId(),
                                String.format("A content needs to be approved for page ID %d",
                                        pageVersion.getPage().getId()),
                                NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(),
                                pageVersion.getPage().getId()));
                    }
                }
                break;
            }
        }
        auditLogService.createAuditLog(currentUser, EntityType.PAGE.getCode(), pId,
                "submitted a page titled " + pageVersion.getTitle() + " for review.");

        return ReviewDTOMapper.mapToBaseResponse(review);

    }

    public ReviewDTO.UpdatedResponse updateReview(Long pageId, Long versionId, ReviewDTO.UpdateRequest request) {
        AtomicReference<Review> updatedReviewRef = new AtomicReference<>();

        // Validate Permission
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!hasContentApprovalPermission(currentUser))
            throw new RequestValidationException("You are not permitted to review this content.");

        Review review = reviewRepository.getByPageVersionIdAndPageVersionPageId(versionId, pageId);
        if (review == null)
            throw new RequestValidationException("Submit a pending review first");
        PageVersion pageVersion = pageVersionRepository.findByPageIdAndId(pageId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));

        boolean isPending = Objects.equals(review.getStatus(), ReviewStatus.PENDING.getCode());

        Map<String, Long> test = userRepository
                .checkUserPageApprovalPermissionAndGetNextWorkflowStep(currentUser.getId(), pageId, versionId);

        Map<WorkflowStep, Boolean> currentStep = isStepDone(pageVersion);
        List<WorkflowStep> sortedSteps = sortSteps(currentStep.keySet());

        if (isContentDisapproved(pageVersion))
            throw new RequestValidationException("Someone already disapproved the content.");

        for (WorkflowStep step : sortedSteps) {
            boolean isStepDone = currentStep.get(step);

            if (!isStepDone) {
                Set<User> approvers = getStepApprovers(step);
                // Check if the current user is an approver for this step
                if (containsUser(approvers, currentUser)) {
                    // Check if the current user has already approved the content
                    if (!reviewRepository.hasUserApprovedContentInPageVersion(review.getPageVersion(), currentUser)
                            && isPending) {
                        // Update the review with the current step and other information
                        review.setWorkflowStep(step);
                        review.setPageVersion(review.getPageVersion());
                        review.setUser(currentUser);
                        review.setComment(request.comment());
                        review.setReviewDate(LocalDateTime.now());
                        review.setStatus(request.status());
                        updatedReviewRef.set(reviewRepository.save(review));

                        break;
                    }
                } else {
                    throw new RequestValidationException("You are not permitted to approve this content yet.");
                }
            }
        }
        if (request.status().equals(ReviewStatus.PENDING.getCode())) {
            throw new RequestValidationException("Content is already submitted as pending.");
        }

        long pageAuthorId = review.getPageVersion().getPage().getAuthor().getId();
        long pId = review.getPageVersion().getPage().getId();

        // Notify Author
        if (request.status().equals(ReviewStatus.APPROVED.getCode())) {
            notificationService.createNotification(new NotificationDTO.BaseRequest(pageAuthorId, currentUser.getId(),
                    String.format("Your Content has been Approved by %s!", currentUser.getEmail()),
                    NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(), pId));
        } else if (request.status().equals(ReviewStatus.DISAPPROVED.getCode())) {
            notificationService.createNotification(new NotificationDTO.BaseRequest(pageAuthorId, currentUser.getId(),
                    String.format("Your content has been disapproved by %s due to ", currentUser.getEmail())
                            + review.getComment(),
                    NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(), pId));
        }

        // Implement audit log
        auditLogService.createAuditLog(currentUser, EntityType.PAGE.getCode(), pId, "reviewed a content");

        // Retrieve the updated review from the AtomicReference
        Review updatedReview = updatedReviewRef.get();

        // Handle the case when no review is updated
        if (updatedReview == null) {
            throw new RequestValidationException("No review was updated.");
        } else {
            if (!isContentFullyApproved(review.getPageVersion())) {

                if (!isContentDisapproved(review.getPageVersion())) {
                    Review newReview = new Review();
                    newReview.setPageVersion(review.getPageVersion());
                    newReview.setUser(null);
                    newReview.setWorkflowStep(null);
                    newReview.setComment("");
                    newReview.setReviewDate(LocalDateTime.now());
                    newReview.setStatus(ReviewStatus.PENDING.getCode());

                    reviewRepository.save(newReview);
                }
            }
        }

        for (Map.Entry<String, Long> entry : test.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        return ReviewDTOMapper.mapToUpdatedResponse(updatedReview);
    }

    private boolean containsUser(Set<User> set, User user) {
        for (User item : set) {
            if (item.equals(user)) {
                return true;
            }
        }
        return false;
    }

    private List<WorkflowStep> sortSteps(Set<WorkflowStep> steps) {
        return steps.stream()
                .sorted(Comparator.comparingInt(WorkflowStep::getStep))
                .collect(Collectors.toList());
    }

    private Set<User> getStepApprovers(WorkflowStep step) {
        return step.getApprovers()
                .stream()
                .map(WorkflowStepApprover::getApprover)
                .collect(Collectors.toSet());
    }

    private Map<WorkflowStep, Boolean> isStepDone(PageVersion pageVersion) {
        Map<WorkflowStep, Boolean> data = new HashMap<>();
        Set<WorkflowStep> steps = pageVersion.getPage().getDirectory().getWorkflow().getSteps();

        List<WorkflowStep> sortedSteps = sortSteps(steps);

        sortedSteps.forEach(step -> {
            Set<User> approvers = getStepApprovers(step);
            boolean isAnyApproverApproved = approvers.stream()
                    .anyMatch(approver -> reviewRepository.hasUserApprovedContentInPageVersion(pageVersion, approver));
            data.put(step, isAnyApproverApproved);
        });

        return data;
    }

    private boolean isContentFullyApproved(PageVersion pageVersion) {
        Map<WorkflowStep, Boolean> stepApprovalStatus = isStepDone(pageVersion);

        for (boolean isStepDone : stepApprovalStatus.values()) {
            if (!isStepDone) {
                return false;
            }
        }
        return true;
    }

    private boolean isContentDisapproved(PageVersion pageVersion) {
        Set<WorkflowStep> steps = pageVersion.getPage().getDirectory().getWorkflow().getSteps();

        List<WorkflowStep> sortedSteps = sortSteps(steps);

        for (WorkflowStep step : sortedSteps) {
            Set<User> approvers = getStepApprovers(step);
            boolean isAnyApproverDisapproved = approvers.stream()
                    .anyMatch(approver -> reviewRepository.hasUserReviewedContentAsDisapprovedInPageVersion(pageVersion,
                            approver));

            if (isAnyApproverDisapproved) {
                return true; // If someone has disapproved the content, return true
            }
        }

        return false; // If no one has disapproved the content, return false
    }

    private boolean pagePermissionGranted(Long pageId, String permission) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPermissionValidatorService.userHasPagePermission(currentUser.getId(), pageId,
                permission);
    }

    public boolean hasContentApprovalPermission(User user) {
        Set<Role> roles = user.getRole();

        // Check if any role has the "CONTENT_APPROVAL" permission
        return roles.stream()
                .flatMap(role -> role.getUserPermissions().stream())
                .anyMatch(permission -> permission.getName().equals("CONTENT_APPROVAL"));
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getReviewRequestForUser(int page, int size, String searchKey,
            String status) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        searchKey = "%" + searchKey + "%";

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewForUser = reviewRepository.findAllByUser(currentUser, status, searchKey, pageRequest);

        if (reviewForUser.hasContent()) {
            List<ReviewDTO.BaseResponse> reviewDTOS = reviewForUser
                    .stream().map(ReviewDTOMapper::mapToBaseResponse).collect(Collectors.toList());

            PaginatedResponse<ReviewDTO.BaseResponse> result = new PaginatedResponse<>(reviewDTOS, page, size,
                    reviewForUser.getTotalElements());
            return result;
        } else {
            throw new NoContentException("No reviews retrieved for this user.");
        }
    }

    public List<ReviewDTO.ApproverResponse> getReviewsByPage(Long pageId, Long pageVersion) {
        List<Review> reviews = reviewRepository.findAllApprovedByPageIdAndPageVersionId(pageVersion, pageId);
        if (!reviews.isEmpty()) {
            List<ReviewDTO.ApproverResponse> reviewDTOs = reviews.stream()
                    .map(ReviewDTOMapper::mapToApproverResponse)
                    .collect(Collectors.toList());

            return reviewDTOs;
        } else {
            throw new NoContentException("No reviews retrieved for this user.");
        }
    }

    private WorkflowStep getIncompleteStep(PageVersion pageVersion) {
        Map<WorkflowStep, Boolean> stepStatusMap = isStepDone(pageVersion);

        for (Map.Entry<WorkflowStep, Boolean> entry : stepStatusMap.entrySet()) {
            WorkflowStep step = entry.getKey();
            boolean isDone = entry.getValue();

            if (!isDone) {
                return step;
            }
        }

        return null;
    }

    public ReviewDTO.CanApproveResponse CanApproverApproveContent(Long pageId, Long versionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));
        Review review = reviewRepository.getByPageVersionIdAndPageVersionPageId(versionId, pageId);
        PageVersion pageVersion = pageVersionRepository.findByPageIdAndId(pageId, versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));
        if (!hasContentApprovalPermission(user))
            throw new RequestValidationException("You are not permitted to review this content.");

        WorkflowStep step = getIncompleteStep(pageVersion);

        Set<User> approvers = getStepApprovers(step);
        boolean canApprove = approvers.contains(user);

        return ReviewDTOMapper.mapToCanApproveResponse(review, user, canApprove);
    }

    public Map<String, Long> canApproverApproveContent(Long pageId, Long versionId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var canReview = userPermissionValidatorService
                .checkUserPageApprovalPermissionAndGetNextWorkflowStep(currentUser.getId(), pageId, versionId);

        if (canReview.isEmpty()) {
            canReview.put("can_approve", 0L);
            canReview.put("page_version_id", versionId);
            canReview.put("page_id", pageId);
            return canReview;
        }

        var newMap = new HashMap<String, Long>();
        newMap.put("can_approve", 1L);
        newMap.put("page_version_id", versionId);
        newMap.put("page_id", pageId);
        return newMap;
    }

}
