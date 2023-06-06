package com.yondu.knowledgebase.services.implementations;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalUpvoteDTO;
import com.yondu.knowledgebase.entities.Rating;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.repositories.RatingRepository;
import com.yondu.knowledgebase.services.RatingService;

@Service
public class RatingServiceImpl implements RatingService {
	private final RatingRepository ratingRepository;
	
	public RatingServiceImpl(RatingRepository ratingRepository) {
		this.ratingRepository = ratingRepository;
	}

	private User getCurrentUser() {
		try {
			return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}catch(Exception e) {
			throw new NoContentException("User must be logged-in first.");
		}
	}
	
	private Rating getSavedRating(Long userId, Long entityId, String entityTpe) {
		return ratingRepository.findByUserIdEntityIdEntityType(userId, entityId, entityTpe);
	}

	@Override
	public ApiResponse<RatingDTO> createRating(RatingDTO ratingDto) {
		ratingDto.setUser_id(getCurrentUser().getId());
		if(ratingRepository.isRecordExistAndActive(getCurrentUser().getId(), ratingDto.getEntity_id(), ratingDto.getEntity_type()) == 1) {
			Rating savedRating = getSavedRating(getCurrentUser().getId(), ratingDto.getEntity_id(), ratingDto.getEntity_type());
			if(!ratingDto.getRating().equals(savedRating.getRating())) {
				savedRating.setRating(ratingDto.getRating());
				ratingRepository.save(savedRating);
				return ApiResponse.success(ratingDto, String.format("Rating for %s with id# %d has been updated.", ratingDto.getEntity_type(), ratingDto.getEntity_id()));
			}else {
				throw new DuplicateResourceException("RECORD ALREADY EXIST.");
			}
		}else if(ratingRepository.isRecordExistAndInactive(getCurrentUser().getId(), ratingDto.getEntity_id(), ratingDto.getEntity_type()) == 1){
			Rating savedRating = getSavedRating(getCurrentUser().getId(), ratingDto.getEntity_id(), ratingDto.getEntity_type());
			savedRating.setActive(true);
			savedRating.setRating(ratingDto.getRating());
			ratingRepository.save(savedRating);
			return ApiResponse.success(ratingDto, String.format("Rating for %s with id# %d has been updated and set to active.", ratingDto.getEntity_type(), ratingDto.getEntity_id()));
		}else {
			Rating userRating = new Rating();
			userRating.setUser(getCurrentUser());
			userRating.setEntity_id(ratingDto.getEntity_id());
			userRating.setEntity_type(ratingDto.getEntity_type());
			userRating.setRating(ratingDto.getRating());
			ratingRepository.save(userRating);
	    	return ApiResponse.success(ratingDto,String.format("Rating for %s with id# %d has been created.", ratingDto.getEntity_type(), ratingDto.getEntity_id()));
		}
	}

	@Override
	public ApiResponse<RatingDTO> updateRating(RatingDTO ratingDto) {
		Rating savedRating = getSavedRating(getCurrentUser().getId(), ratingDto.getEntity_id(), ratingDto.getEntity_type());
		savedRating.setActive(false);
		ratingRepository.save(savedRating);
		ratingDto.setUser_id(getCurrentUser().getId());
		return ApiResponse.success(ratingDto, String.format("Rating for %s with id# %d has been deactivated", ratingDto.getEntity_type(), ratingDto.getEntity_id()));
	}

	@Override
	public TotalUpvoteDTO totalUpvote(RatingDTO ratingDto) {
		Integer upvote = ratingRepository.countUpvoteByEntityIdAndEntityType(ratingDto.getEntity_id(), ratingDto.getEntity_type());
		TotalUpvoteDTO totalUpvote = new TotalUpvoteDTO();
		totalUpvote.setEntity_id(ratingDto.getEntity_id());
		totalUpvote.setEntity_type(ratingDto.getEntity_type());
		totalUpvote.setTotal_upvote(upvote);
		return totalUpvote;
	}
}
