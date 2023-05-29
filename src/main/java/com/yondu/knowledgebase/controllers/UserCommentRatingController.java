package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.exceptions.BadRequestException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
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
    public ResponseEntity<ApiResponse<?>> addCommentRating (@RequestBody UserCommentRatingDTO userCommentRatingDTO) {
        try {
            String ratingValue = userCommentRatingDTO.getRating();

            if (!(ratingValue.equals("UP") || ratingValue.equals("DOWN"))) {
                throw new BadRequestException("Invalid rating value");
            }

            UserCommentRating userCommentRating = userCommentRatingService.rateComment(userCommentRatingDTO.getCommentId(), userCommentRatingDTO.getUserId(), userCommentRatingDTO.getRating());

            if (userCommentRating == null) {
                throw new NotFoundException("User / Comment ID Not Found");
            }

            int totalCommentRating = userCommentRatingService.getTotalCommentRating(userCommentRatingDTO.getCommentId());

            UserCommentRatingDTO userCommentRatingResponse = new UserCommentRatingDTO(userCommentRatingDTO.getUserId(), userCommentRatingDTO.getCommentId(), ratingValue,totalCommentRating);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "Rating successful"));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }
}
