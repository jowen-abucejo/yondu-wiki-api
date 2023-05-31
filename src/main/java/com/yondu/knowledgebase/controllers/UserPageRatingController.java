package com.yondu.knowledgebase.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yondu.knowledgebase.DTO.UserPageRatingDTO;
import com.yondu.knowledgebase.entities.UserPageRating;
import com.yondu.knowledgebase.services.UserPageRatingService;

@Controller
public class UserPageRatingController {

	@Autowired
	private UserPageRatingService userPageRatingService;

	@PostMapping("/rate-page/{page-id}")
    public ResponseEntity<UserPageRating> ratePage(@RequestBody UserPageRatingDTO rating, @PathVariable(value="page-id") Long id){
        return userPageRatingService.ratePageService(rating, id);
    }
}