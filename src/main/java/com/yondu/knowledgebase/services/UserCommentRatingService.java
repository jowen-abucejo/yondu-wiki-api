package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.UserCommentRating;

import java.util.List;

public interface UserCommentRatingService {

    public UserCommentRating addCommentRating (UserCommentRatingDTO UserCommentRatingDTO);

}
