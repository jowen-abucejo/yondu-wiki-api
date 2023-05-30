package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.UserCommentRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        try {
            String ratingValue = userCommentRatingDTO.getRating();

            if (!(ratingValue.equals("UP") || ratingValue.equals("DOWN"))) {
                throw new RequestValidationException("Invalid rating value");
            }

            UserCommentRating userCommentRating = userCommentRatingService.rateComment(userCommentRatingDTO.getCommentId(), userCommentRatingDTO.getUserId(), userCommentRatingDTO.getRating());

            if (userCommentRating == null) {
                throw new ResourceNotFoundException("User / Comment ID Not Found");
            }

            int totalCommentRating = userCommentRatingService.getTotalCommentRating(userCommentRatingDTO.getCommentId());

            UserCommentRatingDTO userCommentRatingResponse = new UserCommentRatingDTO(userCommentRatingDTO.getUserId(), userCommentRatingDTO.getCommentId(), ratingValue,totalCommentRating);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatingResponse, "Rating successful"));

        } catch (RequestValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCommentRating (){
        try{
            List <UserCommentRating> userCommentRatings = userCommentRatingService.getAllCommentRating();
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRatings, "All ratings retrieved"));
        }catch (RequestValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping ("/{ratingId}")
    public ResponseEntity<ApiResponse<?>> getCommentRatingById (@PathVariable Long ratingId){
        try{
            UserCommentRating userCommentRating = userCommentRatingService.getCommentRating(ratingId);
            if(userCommentRating == null)
                throw new ResourceNotFoundException("Rating not found");
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRating, "Rating retrieved successfully"));
        }catch (RequestValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }

    @PutMapping ("/update/{ratingId}")
    public ResponseEntity <ApiResponse<?>> updateRating (@RequestBody String rating, @PathVariable Long ratingId){
        try{
            UserCommentRating userCommentRating = userCommentRatingService.updateRating(rating, ratingId);
            if(userCommentRating == null)
                throw new NotFoundException("Rating not found");
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCommentRating,"Rating successfully updated"));
        }catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }
}
