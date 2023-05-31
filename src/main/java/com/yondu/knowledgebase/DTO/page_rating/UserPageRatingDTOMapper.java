package com.yondu.knowledgebase.DTO.page_rating;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.entities.UserPageRating;

@Service
public class UserPageRatingDTOMapper implements Function<UserPageRating, UserPageRatingDTO> {

	@Override
	public UserPageRatingDTO apply(UserPageRating userPageRating) {
		return new UserPageRatingDTO(
				userPageRating.getRating(),
				userPageRating.getUser().getId(), 
				userPageRating.getPage().getId()
				);
	}
}
