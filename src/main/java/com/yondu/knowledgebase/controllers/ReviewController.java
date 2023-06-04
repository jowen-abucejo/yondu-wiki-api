package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.entities.Review;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yondu.knowledgebase.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDTO.BaseResponse>>> getAllReviews() {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(reviewService.getAllReviews(), "Success retrieving list of page reviews"));
    }

@PostMapping("/pages/{pageId}/versions/{versionId}/reviews")
    public ResponseEntity<ApiResponse<?>> createReview(@PathVariable("pageId") Long pageId, @PathVariable("versionId") Long versionId) {
        Object data = reviewService.createReview(pageId, versionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Review submitted successfully"));


}
    @PutMapping("/reviews/update/{id}")
    @PreAuthorize("hasAuthority('CONTENT_APPROVAL')")
    public ResponseEntity<ApiResponse<?>> updateReview(@PathVariable("id") Long id, @RequestBody ReviewDTO.UpdateRequest request) {
            Object data = reviewService.updateReview(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Review updated successfully"));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO.BaseResponse>> getReviewById(@PathVariable Long id){
        ReviewDTO.BaseResponse review = reviewService.getReview(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(review, "Review with id: " + id + " found"));
    }

    @GetMapping("reviews/search")
    public ResponseEntity<?> getReviewByStatus(@RequestParam(defaultValue = "PENDING") String status, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size){
        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviewsByStatus(status,page,size);

        ApiResponse apiResponse = ApiResponse.success(review, "Retrieved reviews successfully");
        return ResponseEntity.ok(apiResponse);
    }
}
