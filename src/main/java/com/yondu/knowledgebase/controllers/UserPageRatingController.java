package com.yondu.knowledgebase.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yondu.knowledgebase.DTO.page_rating.UserPageRatingDTO;
import com.yondu.knowledgebase.services.UserPageRatingService;

@RestController
@RequestMapping(path="ratings")
public class UserPageRatingController {

	@Autowired
	private UserPageRatingService userPageRatingService;

	@PostMapping("/{id}")
    public ResponseEntity<UserPageRatingDTO> create(@RequestBody UserPageRatingDTO rating, @PathVariable(value="id") Long id){
        return userPageRatingService.createPageRating(rating, id);
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<UserPageRatingDTO> update(@RequestBody UserPageRatingDTO rating, @PathVariable(value="id") Long id){
        return userPageRatingService.updatePageRating(rating, id);
    }
	
	@GetMapping("/{id}/pages")
    public List<UserPageRatingDTO> retrieveRatingByPageId(@PathVariable(value="id") Long id){
		return userPageRatingService.getRatingByPageId(id);
    }
	
	@GetMapping("/{id}/users")
    public List<UserPageRatingDTO> retrieveRatingByUserId(@PathVariable(value="id") Long id){
		return userPageRatingService.getRatingByUserId(id);
    }

	@GetMapping("/pages")
    public List<UserPageRatingDTO> retrieveAllPageRating(){
		return userPageRatingService.findAllRating();
    }
	
}