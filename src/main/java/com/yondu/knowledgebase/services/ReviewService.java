package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.DTO.review.ReviewDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.enums.*;
import com.yondu.knowledgebase.enums.Permission;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PageVersionRepository pageVersionRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserPermissionValidatorService userPermissionValidatorService;
    private final AuditLogService auditLogService;
    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository workflowStepRepository;



    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PageVersionRepository pageVersionRepository, NotificationService notificationService, UserRepository userRepository, UserPermissionValidatorService userPermissionValidatorService, AuditLogService auditLogService, WorkflowRepository workflowRepository, WorkflowStepRepository workflowStepRepository) {
        this.reviewRepository = reviewRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.userPermissionValidatorService = userPermissionValidatorService;
        this.auditLogService = auditLogService;
        this.workflowRepository = workflowRepository;
        this.workflowStepRepository = workflowStepRepository;
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviews( int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAll(pageRequest);
        List<Review> reviews = reviewPages.getContent();

        List<ReviewDTO.BaseResponse> review = reviews.stream()
                .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(review, page,size, (long)review.size());
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviewsByStatus(String status, int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAllByStatus(ReviewStatus.valueOf(status), pageRequest);
        List<Review> reviews = reviewPages.getContent();

        if(reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found");
        }

            List<ReviewDTO.BaseResponse> review = reviews.stream()
                    .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                    .collect(Collectors.toList());
        return new PaginatedResponse<>(review, page,size, (long)review.size());
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviewsByPageTitle(String title, int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAllByPageVersionTitle("%" + title.toLowerCase() + "%", pageRequest);
        List<Review> reviews = reviewPages.getContent();

        if(reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found");
        }
        List<ReviewDTO.BaseResponse> review = reviews.stream()
                .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(review, page,size, (long)review.size());
    }

    public ReviewDTO.BaseResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review with id " + reviewId + " not found"));

        return ReviewDTOMapper.mapToBaseResponse(review);
    }

    public ReviewDTO.BaseResponse createReview(Long pageId, Long versionId) {
        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageVersion pageVersion = pageVersionRepository.findByPageIdAndId(pageId,versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));

        if (reviewRepository.existsByPageVersionAndStatus(pageVersion, ReviewStatus.PENDING.getCode())) {
            throw new RequestValidationException("A pending review already exists for this page version.");
        }

        boolean pageOwner = currentUser.getId().equals(pageVersion.getPage().getAuthor().getId());
        System.out.println(pageOwner);
        long pId = pageVersion.getPage().getId();

        String requiredPermission = Permission.CREATE_CONTENT.getCode();

        if (!pagePermissionGranted(pId,requiredPermission)) {
            if (!pageOwner) throw new RequestValidationException("You are not permitted to submit this page.");}
         else if (!pagePermissionGranted(pId, Permission.UPDATE_CONTENT.getCode())) {
            if (!pageOwner) throw new RequestValidationException("You are not permitted to submit this page.");}



            Review review = new Review();
            review.setPageVersion(pageVersion);
            review.setUser(null);
            review.setWorkflowStep(null);
            review.setComment("");
            review.setReviewDate(LocalDate.now());
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
                        notificationService.createNotification(new NotificationDTO.BaseRequest(approver.getId(), review.getPageVersion().getPage().getAuthor().getId(),
                                String.format("A content needs to be approved for page ID %d", pageVersion.getPage().getId()),
                                NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(), pageVersion.getPage().getId()));
                    }
                }
                break;
            }
        }
            auditLogService.createAuditLog(currentUser, EntityType.PAGE.getCode(),pId,"submitted a page titled "+pageVersion.getTitle()+" for review.");

            return ReviewDTOMapper.mapToBaseResponse(review);



    }
    public ReviewDTO.UpdatedResponse updateReview(Long pageId, Long versionId, ReviewDTO.UpdateRequest request) {
        AtomicReference<Review> updatedReviewRef = new AtomicReference<>();

        // Validate Permission
        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!hasContentApprovalPermission(currentUser)) throw new RequestValidationException("You are not permitted to review this content.");

        Review review = reviewRepository.getByPageVersionIdAndPageVersionPageId(versionId,pageId);
        PageVersion pageVersion = pageVersionRepository.findByPageIdAndId(pageId,versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));
        Set<WorkflowStep> steps =  pageVersion.getPage().getDirectory().getWorkflow().getSteps();

        // Sort the steps based on the step number in ascending order
        List<WorkflowStep> sortedSteps = sortSteps(steps);

        AtomicInteger approvedApprovers = new AtomicInteger(0);
        AtomicBoolean isReviewSaved = new AtomicBoolean(false);
        boolean isPending = Objects.equals(review.getStatus(), ReviewStatus.PENDING.getCode());

        Map <String, Long> test = userRepository.checkUserPageApprovalPermissionAndGetNextWorkflowStep(currentUser.getId(), pageId,versionId);



        sortedSteps.forEach(step -> {
            Set<User> approvers = getStepApprovers(step);
            int totalApprovers = approvers.size();

            approvers.forEach(approver -> {
                if (reviewRepository.hasUserApprovedContentInPageVersion(review.getPageVersion(), approver)) {
                    approvedApprovers.incrementAndGet();

                    if(approver.equals(currentUser)) {
                        throw new RequestValidationException("You already Approved the Content.");
                    }
                }
                if (reviewRepository.hasUserReviewedContentAsDisapprovedInPageVersion(review.getPageVersion(), approver)) {
                    throw new RequestValidationException("Your content was disapproved by a content approver, resubmit your content again.");
                }
                if(approver.equals(currentUser)&&isPending) {
                    review.setWorkflowStep(step);
                    review.setPageVersion(review.getPageVersion());
                    review.setUser(currentUser);
                    review.setComment(request.comment());
                    review.setReviewDate(LocalDate.now());
                    review.setStatus(request.status());
                    updatedReviewRef.set(reviewRepository.save(review));

                    if (reviewRepository.save(review) != null) {
                        isReviewSaved.set(true);
                        updatedReviewRef.set(review);
                    }
                }
            });

            if(isReviewSaved.get()) {
                System.out.println("Proceed");
            } else if (approvedApprovers.get() != totalApprovers) {
                throw new RequestValidationException("Your content does not have enough approval from the required approvers from step "+step.getStep());
            } else {
                // Reset approvedApprovers count for the next sortedStep
                approvedApprovers.set(0);
            }
        });

        if(request.status().equals(ReviewStatus.PENDING.getCode())) {
            throw new RequestValidationException("Content is already submitted as pending.");
        }

        long pageAuthorId = review.getPageVersion().getPage().getAuthor().getId();
        long pId = review.getPageVersion().getPage().getId();

    // Notify Author
    if (request.status().equals(ReviewStatus.APPROVED.getCode())) {
        notificationService.createNotification(new NotificationDTO.BaseRequest(pageAuthorId, currentUser.getId(),
                String.format("Your Content has been Approved by %s!", currentUser.getEmail()), NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(),pId));
    } else if (request.status().equals(ReviewStatus.DISAPPROVED.getCode())){
        notificationService.createNotification(new NotificationDTO.BaseRequest(pageAuthorId, currentUser.getId(),
                String.format("Your content has been disapproved by %s due to ", currentUser.getEmail())+review.getComment(), NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(),pId));
    }

    // Implement audit log
    auditLogService.createAuditLog(currentUser,EntityType.PAGE.getCode(), pId,"reviewed a content");

        // Retrieve the updated review from the AtomicReference
        Review updatedReview = updatedReviewRef.get();

        // Handle the case when no review is updated
        if (updatedReview == null) {
            throw new RequestValidationException("No review was updated.");
        }

        for (Map.Entry<String, Long> entry : test.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        return ReviewDTOMapper.mapToUpdatedResponse(updatedReview);
    }

    private List<WorkflowStep> sortSteps(Set<WorkflowStep> steps) {
        return steps.stream()
                .sorted(Comparator.comparingInt(WorkflowStep::getStep))
                .collect(Collectors.toList());
    }

    private Set<User> getStepApprovers(WorkflowStep step) {
        return step.getStepApprovers()
                .stream()
                .map(WorkflowStepApprover::getApprover)
                .collect(Collectors.toSet());
    }

    private Map<WorkflowStep, AtomicInteger> countApproverWhoApprovedContentPerStep (PageVersion pageVersion) {

        Map<WorkflowStep, AtomicInteger> data = new HashMap<>();
        Set<WorkflowStep> steps =  pageVersion.getPage().getDirectory().getWorkflow().getSteps();

        List<WorkflowStep> sortedSteps = sortSteps(steps);

        sortedSteps.forEach(step -> {
            Set<User> approvers = getStepApprovers(step);
            AtomicInteger approvedApprovers = new AtomicInteger(0);
            approvers.forEach(approver -> {
                if (reviewRepository.hasUserApprovedContentInPageVersion(pageVersion, approver)) {
                    approvedApprovers.incrementAndGet();
                }
            });
                    data.put(step,approvedApprovers);
                    approvedApprovers.set(0);
        });
        return data;
    }
    private Map<WorkflowStep, Boolean> isStepDone(PageVersion pageVersion) {
        Map<WorkflowStep, Boolean> stepStatusMap = new HashMap<>();

        Map<WorkflowStep, AtomicInteger> approverCountMap = countApproverWhoApprovedContentPerStep(pageVersion);

        approverCountMap.forEach((step, approvedApprovers) -> {
            Set<User> approvers = getStepApprovers(step);

            int totalApprovers = approvers.size();
            boolean isDone = (approvedApprovers.get() == totalApprovers);
            stepStatusMap.put(step, isDone);
        });

        return stepStatusMap;
    }

    private boolean pagePermissionGranted(Long pageId, String permission) {
        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPermissionValidatorService.userHasPagePermission(currentUser.getId(), pageId,
                permission);
    }

    public boolean reviewIsPending(PageVersion pageVersion) {
        for (Review pageRev : pageVersion.getReviews()) {
            if (pageRev.getStatus().equals(ReviewStatus.PENDING.getCode())) {
                return true;
            }
        }
        return false;
    }
    public boolean hasContentApprovalPermission(User user) {
        Set<Role> roles = user.getRole();

        // Check if any role has the "CONTENT_APPROVAL" permission
        return roles.stream()
                .flatMap(role -> role.getUserPermissions().stream())
                .anyMatch(permission -> permission.getName().equals("CONTENT_APPROVAL"));
    }


}
