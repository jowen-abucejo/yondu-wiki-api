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
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PageVersionRepository pageVersionRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserPermissionValidatorService userPermissionValidatorService;
    private final AuditLogService auditLogService;



    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PageVersionRepository pageVersionRepository, NotificationService notificationService, UserRepository userRepository, UserPermissionValidatorService userPermissionValidatorService, AuditLogService auditLogService) {
        this.reviewRepository = reviewRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.userPermissionValidatorService = userPermissionValidatorService;
        this.auditLogService = auditLogService;
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
        if (pageVersion.getReviews().isEmpty()) {
            Review review = new Review();
            review.setPageVersion(pageVersion);
            review.setUser(null);
            review.setComment("");
            review.setReviewDate(null);
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
        } else {
            throw new RequestValidationException("Content already submitted, submit the latest version instead.");
        }


    }
    public ReviewDTO.UpdatedResponse updateReview(Long id, ReviewDTO.UpdateRequest request) {

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Review id not found: %s", id)));

        long pageAuthorId = review.getPageVersion().getPage().getAuthor().getId();
        long pageId = review.getPageVersion().getPage().getId();

        if (review.getStatus().equals(ReviewStatus.APPROVED.getCode()) || review.getStatus().equals(ReviewStatus.DISAPPROVED.getCode())) {

            throw new RequestValidationException("Content is already reviewed. Submit your latest version instead");
        }

            review.setUser(currentUser);
            review.setComment(request.comment());
            review.setReviewDate(LocalDate.now());
            review.setStatus(request.status());
            Review updatedReview = reviewRepository.save(review);

    if (request.status().equals(ReviewStatus.APPROVED.getCode())) {
        notificationService.createNotification(new NotificationDTO.BaseRequest(pageAuthorId, currentUser.getId(),
                String.format("Your Content has been Approved by %s!", currentUser.getEmail()), NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(),pageId));
    } else if (request.status().equals(ReviewStatus.DISAPPROVED.getCode())){
        notificationService.createNotification(new NotificationDTO.BaseRequest(pageAuthorId, currentUser.getId(),
                String.format("Your content has been disapproved by %s due to ", currentUser.getEmail())+review.getComment(), NotificationType.APPROVAL.getCode(), ContentType.PAGE.getCode(),pageId));
    }

    // Implement audit log
    auditLogService.createAuditLog(currentUser,EntityType.PAGE.getCode(), pageId,"reviewed a content");

        return ReviewDTOMapper.mapToUpdatedResponse(updatedReview);
    }

    private boolean pagePermissionGranted(Long pageId, String permission) {
        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPermissionValidatorService.userHasPagePermission(currentUser.getId(), pageId,
                permission);
    }
}
