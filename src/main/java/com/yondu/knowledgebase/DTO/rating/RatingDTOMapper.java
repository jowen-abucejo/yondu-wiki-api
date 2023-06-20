package com.yondu.knowledgebase.DTO.rating;

import java.util.function.Function;

import com.yondu.knowledgebase.entities.Rating;

public class RatingDTOMapper implements Function<Rating, RatingDTO>{

	@Override
	public RatingDTO apply(Rating rating) {
		return new RatingDTO(
				rating.getUser().getId(),
				rating.getEntity_id(),
				rating.getEntity_type(),
				rating.getRating()
				);
	}

}
