package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.services.UserCommentRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/comment-ratings")
public class UserCommentRatingController {
    private final UserCommentRatingService userCommentRatingService;

    public UserCommentRatingController (UserCommentRatingService userCommentRatingService){
        this.userCommentRatingService = userCommentRatingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> addCommentRating (@RequestBody UserCommentRatingDTO userCommentRatingDTO) {
        UserCommentRatingDTO userCommentRatingResponse = userCommentRatingService.rateComment(userCommentRatingDTO.getCommentId(), userCommentRatingDTO.getUserId(), userCommentRatingDTO.getRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "Rating successful"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCommentRating (){
        List <UserCommentRatingDTO> userCommentRatingResponse = userCommentRatingService.getAllCommentRating();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "All ratings retrieved"));
    }

    @GetMapping ("/{ratingId}")
    public ResponseEntity<ApiResponse<?>> getCommentRatingById (@PathVariable Long ratingId){
        UserCommentRatingDTO userCommentRatingResponse = userCommentRatingService.getCommentRating(ratingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "Rating retrieved successfully"));
    }

    @PutMapping ("/update/{ratingId}")
    public ResponseEntity <ApiResponse<?>> updateRating (@RequestBody UserCommentRatingDTO userCommentRatingDTO, @PathVariable Long ratingId){
        UserCommentRatingDTO userCommentRatingResponse = userCommentRatingService.updateRating(userCommentRatingDTO.getRating(), ratingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse,"Rating successfully updated"));
    }
}
