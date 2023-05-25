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

    @PostMapping("/test")
    public ResponseEntity<?> addRating (@RequestBody UserCommentRatingDTO userCommentRatingDTO){

        String ratingValue = userCommentRatingDTO.getRating();
        System.out.println(ratingValue);

        if(!(ratingValue.equals("UP") || ratingValue.equals("DOWN"))){
            userCommentRatingDTO.setMessage("Invalid rating value");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userCommentRatingDTO);
        }

        UserCommentRating userCommentRating = userCommentRatingService.rateComment(userCommentRatingDTO.getCommentId(), userCommentRatingDTO.getUserId(), userCommentRatingDTO.getRating());

        if(userCommentRating == null){
            userCommentRatingDTO.setMessage("User/Comment ID Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userCommentRatingDTO);
        }

        int totalCommentRating = userCommentRatingService.totalCommentRating(userCommentRatingDTO.getCommentId());

        userCommentRatingDTO.setMessage("Rating Successfully Added");
        UserCommentRatingDTO userCommentRatingResponse = new UserCommentRatingDTO(userCommentRatingDTO.getUserId(),userCommentRatingDTO.getCommentId(),ratingValue,totalCommentRating,userCommentRatingDTO.getMessage());

        return ResponseEntity.status(HttpStatus.OK).body(userCommentRatingResponse);
    }

}
