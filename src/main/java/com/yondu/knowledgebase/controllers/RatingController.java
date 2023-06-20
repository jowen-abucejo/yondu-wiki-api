package com.yondu.knowledgebase.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalUpvoteDTO;
import com.yondu.knowledgebase.services.RatingService;

@RestController
@RequestMapping("ratings")
public class RatingController {
	
	@Autowired
	private RatingService ratingService;
	
	@PostMapping("")
	public ApiResponse<RatingDTO> create(@RequestBody RatingDTO ratingDto) {
		return ratingService.createRating(ratingDto);
	}
	
	@PutMapping("")
	public ApiResponse<RatingDTO> update(@RequestBody RatingDTO ratingDto) {
		return ratingService.updateRating(ratingDto);
	}
	
	@GetMapping("/upvotes")
	public TotalUpvoteDTO totalUpvoteByEntityIdAndEntityType(@RequestParam(value="id") Long entityId, @RequestParam(value="type") String entityType) {
		return ratingService.totalUpvote(entityId, entityType);
	}
	
	@GetMapping("/users/upvotes")
	public RatingDTO checkIfEntityUpvoted(@RequestParam(value="entity_id") Long entityId, @RequestParam(value="type") String entityType) {
		return ratingService.ratingByEntityIdAndEntityType(entityId,entityType);
	}
}
