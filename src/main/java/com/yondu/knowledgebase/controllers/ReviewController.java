package com.yondu.knowledgebase.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.services.ReviewService;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("reviews")
    public ResponseEntity<?> getReviews(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviews(page, size);

        ApiResponse apiResponse = ApiResponse.success(review, "Retrieved reviews successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/pages/{pageId}/versions/{versionId}/reviews")
    public ResponseEntity<ApiResponse<?>> createReview(@PathVariable("pageId") Long pageId,
            @PathVariable("versionId") Long versionId) {
        Object data = reviewService.createReview(pageId, versionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Review submitted successfully"));

    }

    @PutMapping("/pages/{pageId}/versions/{versionId}/reviews")
    public ResponseEntity<ApiResponse<?>> updateReview(@PathVariable("pageId") Long pageId,
            @PathVariable("versionId") Long versionId, @RequestBody ReviewDTO.UpdateRequest request) {
        Object data = reviewService.updateReview(pageId, versionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Review updated successfully"));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO.BaseResponse>> getReviewById(@PathVariable Long id) {
        ReviewDTO.BaseResponse review = reviewService.getReview(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(review, "Review with id: " + id + " found"));
    }

    @GetMapping("reviews/status")
    public ResponseEntity<?> getReviewByStatus(@RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviewsByStatus(status, page, size);

        ApiResponse apiResponse = ApiResponse.success(review, "Retrieved reviews successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("reviews/search")
    public ResponseEntity<?> getReviewByPageTitle(@RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviewsByPageTitle(title, page, size);

        ApiResponse apiResponse = ApiResponse.success(review, "Retrieved reviews successfully");
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Fetches past reviews based
     * on the page version being
     * reviewed.
     *
     *
     * @param pageId      ID of the page.
     * @param pageVersion ID of the page version.
     */
    @GetMapping("pages/{page_id}/versions/{page_version}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDTO.ApproverResponse>>> getReviewsByPage(
            @PathVariable(name = "page_id") Long pageId, @PathVariable(name = "page_version") Long pageVersion) {
        List<ReviewDTO.ApproverResponse> reviews = reviewService.getReviewsByPage(pageId, pageVersion);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Successfully fetched reviews"));
    }

    @GetMapping("pages/{page_id}/versions/{page_version}/reviews/approver/{user_id}")
    public ResponseEntity<ApiResponse<ReviewDTO.CanApproveResponse>> getApproverCanApprove(
            @PathVariable(name = "page_id") Long pageId, @PathVariable(name = "page_version") Long pageVersion,
            @PathVariable(name = "user_id") Long userId) {
        ReviewDTO.CanApproveResponse review = reviewService.CanApproverApproveContent(pageId, pageVersion, userId);
        return ResponseEntity.ok(ApiResponse.success(review, "Successfully fetched review approver"));
    }

    @GetMapping("pages/{page_id}/versions/{page_version}/reviews/is-current-step-approver")
    public ApiResponse<Map<String, Long>> currentUserCanApproveEntity(@PathVariable(name = "page_id") Long pageId,
            @PathVariable(name = "page_version") Long pageVersion) {
        var canApprove = reviewService.canApproverApproveContent(pageId, pageVersion);
        return ApiResponse.success(canApprove, "Successfully check if user can approve");
    }
}
