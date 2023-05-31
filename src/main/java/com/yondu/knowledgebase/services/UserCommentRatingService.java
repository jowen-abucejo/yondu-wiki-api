package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;

import java.util.List;

public interface UserCommentRatingService {

    public UserCommentRatingDTO rateComment (Long commentId, Long userId, String ratingValue);

    List<UserCommentRatingDTO> getAllCommentRating();

    UserCommentRatingDTO getCommentRating(Long ratingId);

    UserCommentRatingDTO updateRating(String rating, Long ratingId);

}
