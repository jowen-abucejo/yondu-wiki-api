package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ReviewCreateDTO;
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
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

@PostMapping("/pages/{pageId}/versions/{versionId}/reviews")
    public ResponseEntity<Review> createReview(@PathVariable("pageId") Long pageId, @PathVariable("versionId") Long versionId) {
        Review createdReview = reviewService.createReview(pageId, versionId);
        return ResponseEntity.created(URI.create("/pages/" + pageId + "/versions/" + versionId + "/reviews/" + createdReview.getId()))
                .body(createdReview);
}
}
