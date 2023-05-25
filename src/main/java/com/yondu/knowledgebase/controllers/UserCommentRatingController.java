package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.services.UserCommentRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment-rating")
public class UserCommentRatingController {
    private final UserCommentRatingService userCommentRatingService;

    public UserCommentRatingController (UserCommentRatingService userCommentRatingService){
        this.userCommentRatingService = userCommentRatingService;
    }

    @PostMapping
    public ResponseEntity<?> addCommentRating (@RequestBody UserCommentRatingDTO userCommentRatingRequest){
        UserCommentRating addedRating = userCommentRatingService.addCommentRating(userCommentRatingRequest);
        if(addedRating!=null){
            userCommentRatingRequest.setMessage("Comment Rating added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(userCommentRatingRequest);
        }else {
            userCommentRatingRequest.setMessage("Failed to add Comment Rating");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userCommentRatingRequest);
        }
    }


}
