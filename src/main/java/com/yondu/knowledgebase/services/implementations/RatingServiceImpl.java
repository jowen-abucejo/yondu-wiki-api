package com.yondu.knowledgebase.services.implementations;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalUpvoteDTO;
import com.yondu.knowledgebase.entities.Notification;
import com.yondu.knowledgebase.entities.Rating;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.NotificationRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.RatingRepository;
import com.yondu.knowledgebase.services.RatingService;

@Service
public class RatingServiceImpl implements RatingService {
	private final RatingRepository ratingRepository;
	private final NotificationRepository notificationRepository;
	private final PageRepository pageRepository;
	private final CommentRepository commentRepository;
	
	public RatingServiceImpl(
			RatingRepository ratingRepository, 
			NotificationRepository notificationRepository,
			PageRepository pageRepository,
			CommentRepository commentRepository
			) {
		this.ratingRepository = ratingRepository;
		this.notificationRepository = notificationRepository;
		this.pageRepository = pageRepository;
		this.commentRepository = commentRepository;
	}

	private User getCurrentUser() {
		try {
			return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}catch(Exception e) {
			throw new NoContentException("User must be logged-in first.");
		}
	}
	
	private User getEntityAuthor(Long entityId, String entityTpe){
		try {
			switch(entityTpe) {
				case "Page":
					return pageRepository.findById(entityId).get().getAuthor();
				case "Comment":
					return commentRepository.findByEntityTypeAndEntityId("Comment",entityId).get(0).getUser();
				default:
					throw new ResourceNotFoundException("Record not found");
			}
		}catch(Exception e) {
			throw new ResourceNotFoundException("Record not found: "+e);
		}
	}
	
	private void notifyPageAuthor(String userVote, Long entityId, String entityTpe) {
		Notification newNotif = new Notification();
    	String vote = userVote.toUpperCase().equals("UP") ? "upvoted" : "downvoted";
    	newNotif.setMessage("Someone "+vote+" your "+entityTpe+".");
    	newNotif.setTimestamp(LocalDateTime.now());
    	newNotif.setRead(false);
    	newNotif.setUser(getEntityAuthor(entityId, entityTpe));
    	notificationRepository.save(newNotif);
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
			notifyPageAuthor(ratingDto.getRating(),ratingDto.getEntity_id(), ratingDto.getEntity_type());
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
	public TotalUpvoteDTO totalUpvote(Long entityId, String entityType) {
		Integer upvote = ratingRepository.countUpvoteByEntityIdAndEntityType(entityId, entityType);
		TotalUpvoteDTO totalUpvote = new TotalUpvoteDTO();
		totalUpvote.setEntity_id(entityId);
		totalUpvote.setEntity_type(entityType);
		totalUpvote.setTotal_upvote(upvote);
		return totalUpvote;
	}

	@Override
	public RatingDTO ratingByEntityIdAndEntityType(Long entityId, String entityType) {
		Rating rating = ratingRepository.findByUserIdEntityIdEntityType(getCurrentUser().getId(), entityId, entityType);
		System.out.print(rating);
		RatingDTO ratingDto = new RatingDTO();
		ratingDto.setUser_id(getCurrentUser().getId());
		ratingDto.setEntity_id(entityId);
		ratingDto.setEntity_type(entityType);
		if(rating == null) {
			return ratingDto;
		}else if(rating.getActive().equals(false)){
			return ratingDto;
		}else {
			ratingDto.setRating(rating.getRating());
			return ratingDto;
		}
	}
}
