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
        UserCommentRating userCommentRating = userCommentRatingService.rateComment(userCommentRatingDTO.getCommentId(), userCommentRatingDTO.getUserId(), userCommentRatingDTO.getRating());
        int totalCommentRating = userCommentRatingService.getTotalCommentRating(userCommentRatingDTO.getCommentId());
        UserCommentRatingDTO userCommentRatingResponse = new UserCommentRatingDTO(userCommentRating.getUser().getId(), userCommentRating.getComment().getId(), userCommentRatingDTO.getRating(),totalCommentRating);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "Rating successful"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCommentRating (){
        List <UserCommentRating> userCommentRatings = userCommentRatingService.getAllCommentRating();
        List<UserCommentRatingDTO> userCommentRatingResponse = new ArrayList<>();
        for (UserCommentRating userCommentRating:userCommentRatings){
            UserCommentRatingDTO userCommentRatingDTO = new UserCommentRatingDTO(userCommentRating.getUser().getId(), userCommentRating.getComment().getId(), userCommentRating.getRating(),userCommentRating.getComment().getTotalCommentRating());
            userCommentRatingResponse.add(userCommentRatingDTO);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "All ratings retrieved"));
    }

    @GetMapping ("/{ratingId}")
    public ResponseEntity<ApiResponse<?>> getCommentRatingById (@PathVariable Long ratingId){
        UserCommentRating userCommentRating = userCommentRatingService.getCommentRating(ratingId);
        UserCommentRatingDTO userCommentRatingResponse = new UserCommentRatingDTO(userCommentRating.getUser().getId(), userCommentRating.getComment().getId(), userCommentRating.getRating(),userCommentRating.getComment().getTotalCommentRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "Rating retrieved successfully"));
    }

    @PutMapping ("/update/{ratingId}")
    public ResponseEntity <ApiResponse<?>> updateRating (@RequestBody UserCommentRatingDTO userCommentRatingDTO, @PathVariable Long ratingId){
        UserCommentRating userCommentRating = userCommentRatingService.updateRating(userCommentRatingDTO.getRating(), ratingId);
        int totalCommentRating = userCommentRatingService.getTotalCommentRating(userCommentRating.getComment().getId());
        UserCommentRatingDTO userCommentRatingResponse = new UserCommentRatingDTO(userCommentRating.getUser().getId(), userCommentRating.getComment().getId(), userCommentRatingDTO.getRating(),totalCommentRating);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse,"Rating successfully updated"));
    }
}
