package com.yondu.knowledgebase.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.DTO.UserPageRatingDTO;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserPageRating;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.UserPageRatingRepository;
import com.yondu.knowledgebase.repositories.UserRepository;

@Service
public class UserPageRatingService {

	@Autowired
	private UserPageRatingRepository userPageRatingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PageRepository pageRepository;
	
	public ResponseEntity<UserPageRating> ratePageService(UserPageRatingDTO userRating, Long pageId){

		//current logged-in user
//    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
//    	User currentUser = userRepository.getUserByEmail(username);
//    	userRating.setUserId(currentUser.getId());

		User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userRating.setUserId(currentUser.getId());
		
    	//validation to prevent multiple rating input and update rating if changed.
    	UserPageRating savedRating = userPageRatingRepository.findByPageIdAndUserId(pageId, userRating.getUserId());
    	if(savedRating != null) {
    		if(savedRating.getRating().equals(userRating.getRating().toUpperCase())) {
    			//rating will be deleted if the request have same rating(user can undo their rating)
    			userPageRatingRepository.delete(savedRating);
    			return ResponseEntity.status(HttpStatus.OK).body(savedRating);
    		}else {
    			//update rating
    			savedRating.setRating(userRating.getRating().toUpperCase());
    			userPageRatingRepository.save(savedRating);
    			return ResponseEntity.status(HttpStatus.OK).body(savedRating);
    		}	
    	}else {
    		UserPageRating newUserPageRating = new UserPageRating();
    		newUserPageRating.setRating(userRating.getRating().toUpperCase());
    		newUserPageRating.setUser(currentUser);
    		
    		//selected page to rate
        	Optional<Page> page = pageRepository.findByIdAndActive(pageId, true);
    		newUserPageRating.setPage(page.get());

    		userPageRatingRepository.save(newUserPageRating);
        	return ResponseEntity.status(HttpStatus.CREATED).body(newUserPageRating);
    	}
    }
}