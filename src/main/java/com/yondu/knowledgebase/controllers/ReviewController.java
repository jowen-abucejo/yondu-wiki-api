package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.ReviewCreateDTO;
import com.yondu.knowledgebase.DTO.directory.DirectoryDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.exceptions.BadRequestException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yondu.knowledgebase.entities.Review;
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
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(reviewService.getAllReviews(), "Success retrieving list of page reviews"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to retrieve list of reviews!"));
        }
    }

@PostMapping("/pages/{pageId}/versions/{versionId}/reviews")
    public ResponseEntity<ApiResponse<?>> createReview(@PathVariable("pageId") Long pageId, @PathVariable("versionId") Long versionId) {

    try {
        Object data = reviewService.createReview(pageId, versionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Review submitted successfully"));
    } catch (Exception e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to create review!"));
    }

}
    @PutMapping("/reviews/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateReview(@PathVariable("id") Long id, @RequestBody ReviewDTO.UpdateRequest request) {
        try {
            Object data = reviewService.updateReview(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Review updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to update Review"));
        }

    }
}
