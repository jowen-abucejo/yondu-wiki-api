package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.services.UserCommentRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/comment-rating")
public class UserCommentRatingController {
    private final UserCommentRatingService userCommentRatingService;

    public UserCommentRatingController (UserCommentRatingService userCommentRatingService){
        this.userCommentRatingService = userCommentRatingService;
    }

    @PostMapping
    public ResponseEntity<?> addCommentRating (@RequestBody UserCommentRatingDTO userCommentRatingDTO){
        String ratingValue = userCommentRatingDTO.getRating();

        if(!(ratingValue.equals("UP") || ratingValue.equals("DOWN"))){
            userCommentRatingDTO.setMessage("Invalid rating value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userCommentRatingDTO);
        }

        UserCommentRating userCommentRating = userCommentRatingService.rateComment(userCommentRatingDTO.getCommentId(), userCommentRatingDTO.getUserId(), userCommentRatingDTO.getRating());

        if(userCommentRating == null){
            userCommentRatingDTO.setMessage("User / Comment ID Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userCommentRatingDTO);
        }

        int totalCommentRating = userCommentRatingService.totalCommentRating(userCommentRatingDTO.getCommentId());

        userCommentRatingDTO.setMessage("Rating Successful");
        UserCommentRatingDTO userCommentRatingResponse = new UserCommentRatingDTO(userCommentRatingDTO.getUserId(),userCommentRatingDTO.getCommentId(),ratingValue,totalCommentRating,userCommentRatingDTO.getMessage());

        return ResponseEntity.status(HttpStatus.OK).body(userCommentRatingResponse);
    }
}
