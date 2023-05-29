package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ReviewCreateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yondu.knowledgebase.entities.Review;
import com.yondu.knowledgebase.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/pages/{pageid}/versions/{versionid}/reviews")
    public ResponseEntity<Review> createReview(
            @PathVariable("pageid") Long pageId,
            @PathVariable("versionid") Long versionId,
            @RequestBody ReviewCreateDTO reviewCreateDTO) {
        Review review = reviewService.createReview(pageId, versionId, reviewCreateDTO);
        return ResponseEntity.ok(review);
    }
}
