package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalUpvoteDTO;

public interface RatingService {
	public ApiResponse<RatingDTO> createRating(RatingDTO ratingDto);

	public ApiResponse<RatingDTO> updateRating(RatingDTO ratingDto);

	public TotalUpvoteDTO totalUpvote(RatingDTO ratingDto);
}
