package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;

import java.util.List;

public interface UserCommentRatingService {

    public UserCommentRating rateComment (Long commentId, Long userId, String ratingValue);

    public int getTotalCommentRating(Long commentId);

    List<UserCommentRating> getAllCommentRating();

    UserCommentRating getCommentRating(Long ratingId);

    UserCommentRating updateRating(String rating, Long ratingId);
}
