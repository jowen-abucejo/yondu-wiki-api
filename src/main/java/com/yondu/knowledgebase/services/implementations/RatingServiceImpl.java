package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.services.NotificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalUpvoteDTO;
import com.yondu.knowledgebase.entities.Rating;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.repositories.NotificationRepository;
import com.yondu.knowledgebase.repositories.RatingRepository;
import com.yondu.knowledgebase.services.RatingService;


@Service
public class RatingServiceImpl implements RatingService {
	private final RatingRepository ratingRepository;
	private final NotificationRepository notificationRepository;
	private final CommentRepository commentRepository;
	private final PageRepository pageRepository;
	private final PostRepository postRepository;
	private final PageServiceImpl pageService;
	private final NotificationService notificationService;


	public RatingServiceImpl(RatingRepository ratingRepository, NotificationRepository notificationRepository, CommentRepository commentRepository, PageRepository pageRepository, PostRepository postRepository, PageServiceImpl pageService, NotificationService notificationService) {
		this.ratingRepository = ratingRepository;
		this.notificationRepository = notificationRepository;
		this.pageService = pageService;
		this.pageRepository = pageRepository;
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.notificationService = notificationService;
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
			notifyUser(userRating);
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

	private void notifyUser (Rating rating){
		Long authorId = null;
		String fromUser = rating.getUser().getFirstName() + " " + rating.getUser().getLastName();
		String message = (rating.getRating().equals("UP")) ? String.format("%s added an upvote your to %s", fromUser, rating.getEntity_type().toLowerCase()) : String.format("%s added a downvote to your %s", fromUser, rating.getEntity_type().toLowerCase());
		if(rating.getEntity_type().toUpperCase().equals(ContentType.PAGE.getCode())){
			PageDTO page = pageService.findById(rating.getEntity_id());
			Page selectedPage = pageRepository.findById(page.getId()).orElseThrow(()->new ResourceNotFoundException(String.format("Page ID not found: %d", page.getId())));
			authorId = selectedPage.getAuthor().getId();
		} else if (rating.getEntity_type().toUpperCase().equals(ContentType.POST.getCode())) {
			Post post = postRepository.findById(rating.getEntity_id()).orElseThrow(()->new ResourceNotFoundException(String.format("Post ID not found: %d", rating.getEntity_id())));
			authorId = post.getAuthor().getId();
		} else if (rating.getEntity_type().toUpperCase().equals(ContentType.REPLY.getCode())) {
			Comment comment = commentRepository.findById(rating.getEntity_id()).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", rating.getEntity_id())));
			authorId = comment.getUser().getId();
		}
		notificationService.createNotification(new NotificationDTO.BaseRequest(authorId,rating.getUser().getId(), message, NotificationType.RATE.getCode(), rating.getEntity_type().toUpperCase(), rating.getEntity_id()));
	}
}
