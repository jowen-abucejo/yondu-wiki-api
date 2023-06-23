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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
            if (pageOwner) {System.out.println("Proceed");} else {
                throw new RequestValidationException("You are not permitted to submit this page.");
            }
        } else if (!pagePermissionGranted(pId, Permission.UPDATE_CONTENT.getCode())) {
            if (pageOwner) {System.out.println("Proceed");} else {
                throw new RequestValidationException("You are not permitted to submit this page.");
            }
        }
            Review review = new Review();
            review.setPageVersion(pageVersion);
            review.setUser(null);
            review.setWorkflowStep(null);
            review.setComment("");
            review.setReviewDate(LocalDate.now());
            review.setStatus(ReviewStatus.PENDING.getCode());

            reviewRepository.save(review);

            // Get users with the CONTENT_APPROVAL permission
            Set<User> approvers = userRepository.findUsersWithContentApprovalPermission();

            for (User approver : approvers) {
                System.out.println(approver.getEmail());
                notificationService.createNotification(new NotificationDTO.BaseRequest(approver.getId(), review.getPageVersion().getPage().getAuthor().getId(),
                        String.format("A content needs to be approved for page ID %d", pageVersion.getPage().getId()),
                        NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(), pageVersion.getPage().getId()));
            }
            auditLogService.createAuditLog(currentUser, EntityType.PAGE.getCode(),pId,"submitted a page titled "+pageVersion.getTitle()+" for review.");

            return ReviewDTOMapper.mapToBaseResponse(review);



    }
    public ReviewDTO.UpdatedResponse updateReview(Long pageId, Long versionId, ReviewDTO.UpdateRequest request) {
        AtomicReference<Review> updatedReviewRef = new AtomicReference<>();

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(currentUser);
        if(!hasContentApprovalPermission(currentUser)) throw new RequestValidationException("You are not permitted to review this content.");

        Review review = reviewRepository.getByPageVersionIdAndPageVersionPageId(versionId,pageId);
        PageVersion pageVersion = pageVersionRepository.findByPageIdAndId(pageId,versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));
        Set<WorkflowStep> steps =  pageVersion.getPage().getDirectory().getWorkflow().getSteps();

        // Sort the steps based on the step number in ascending order
        List<WorkflowStep> sortedSteps = steps.stream()
                .sorted(Comparator.comparingInt(WorkflowStep::getStep))
                .collect(Collectors.toList());

        AtomicInteger approvedApprovers = new AtomicInteger(0);
        AtomicBoolean isReviewSaved = new AtomicBoolean(false);
        boolean isPending = Objects.equals(review.getStatus(), ReviewStatus.PENDING.getCode());

        sortedSteps.forEach(step -> {
            Set<User> approvers = step.getStepApprovers()
                    .stream()
                    .map(WorkflowStepApprover::getApprover)
                    .collect(Collectors.toSet());

            int totalApprovers = approvers.size();

            approvers.forEach(approver -> {
                if (reviewRepository.hasUserApprovedContentInPageVersion(review.getPageVersion(), approver)) {
                    approvedApprovers.incrementAndGet();
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

        return ReviewDTOMapper.mapToUpdatedResponse(updatedReview);
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
