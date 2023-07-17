package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalUpvoteDTO;
import com.yondu.knowledgebase.DTO.rating.TotalVoteDTO;

public interface RatingService {
	public ApiResponse<RatingDTO> createRating(RatingDTO ratingDto);

	public ApiResponse<RatingDTO> updateRating(RatingDTO ratingDto);

	public TotalUpvoteDTO totalUpvote(Long entityId, String entityType);

	public RatingDTO ratingByEntityIdAndEntityType(Long entityId, String entityType);
	
	public TotalVoteDTO totalVote(Long entityId, String entityType);
}