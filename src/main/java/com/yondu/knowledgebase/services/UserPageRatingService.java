package com.yondu.knowledgebase.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yondu.knowledgebase.DTO.page_rating.UserPageRatingDTO;
import com.yondu.knowledgebase.DTO.page_rating.UserPageRatingDTOMapper;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserPageRating;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.UserPageRatingRepository;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;

@Service
public class UserPageRatingService {

	@Autowired
	private UserPageRatingRepository userPageRatingRepository;

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private UserPageRatingDTOMapper userPageRatingDTOMapper;
	
	public UserPageRatingService(UserPageRatingRepository userPageRatingRepository, PageRepository pageRepository, UserPageRatingDTOMapper userPageRatingDTOMapper) {
		this.userPageRatingRepository = userPageRatingRepository;
		this.pageRepository = pageRepository;
		this.userPageRatingDTOMapper = userPageRatingDTOMapper;
	}
	
	private User getCurrentUser() {
    	return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public ResponseEntity<UserPageRatingDTO> createPageRating(UserPageRatingDTO userRatingDTO, Long pageId){
    	UserPageRating savedRating = userPageRatingRepository.findByPageIdAndUserId(pageId, getCurrentUser().getId());
    	
    	if(userRatingDTO.getRating().isEmpty()) {
    		throw new NoContentException("Please indicate either UP or DOWN");
    	}else if(savedRating == null) {
    		
    		// selected active page to rate
        	Optional<Page> page = pageRepository.findByIdAndActive(pageId, true);
    		
    		if(page.isPresent()) {
    			// create rating
            	UserPageRating newUserPageRating = new UserPageRating();
        		newUserPageRating.setRating(userRatingDTO.getRating().toUpperCase());
        		newUserPageRating.setUser(getCurrentUser());
        		newUserPageRating.setActive(true);
    			
    			// Append this UserPageRating to Page
    			page.get().getUserPageRatings().add(newUserPageRating);
        		pageRepository.save(page.get());
        		
        		// Set selected Page to this UserPageRating
        		newUserPageRating.setPage(page.get());

        		//save UserPageRating
        		userPageRatingRepository.save(newUserPageRating);
            	return ResponseEntity.status(HttpStatus.CREATED).body(userPageRatingDTOMapper.apply(newUserPageRating));
    		}else {
    			throw new ResourceNotFoundException("Page not found."); 
    		}
    	}else if(!savedRating.getActive()){
    		// set rating to active and set rating value
    		savedRating.setActive(true);
			savedRating.setRating(userRatingDTO.getRating().toUpperCase());
			userPageRatingRepository.save(savedRating);
			return ResponseEntity.status(HttpStatus.OK).body(userPageRatingDTOMapper.apply(savedRating));
    	}else {
    		throw new DuplicateResourceException("Record already exist.");    	
    	}	
    }
	
	public ResponseEntity<UserPageRatingDTO> updatePageRating(UserPageRatingDTO userRatingDTO, Long pageId){
		UserPageRating savedRating = userPageRatingRepository.findByPageIdAndUserId(pageId, getCurrentUser().getId());
		if(!savedRating.getRating().equals(userRatingDTO.getRating().toUpperCase())) {
			// update rating
			if(!savedRating.getActive()) {savedRating.setActive(true);}
			savedRating.setRating(userRatingDTO.getRating().toUpperCase());
			userPageRatingRepository.save(savedRating);
			return ResponseEntity.status(HttpStatus.OK).body(userPageRatingDTOMapper.apply(savedRating));
		}else {
			// if same rating value deactivate rating(to undo rating)
			savedRating.setActive(false);
			userPageRatingRepository.save(savedRating);
			throw new ResponseStatusException(HttpStatus.OK, "Rating deactivated");
		}
	}

	public List<UserPageRatingDTO> getRatingByUserId(Long id){
		return userPageRatingRepository.findByUserId(id)
				.stream().map(userPageRatingDTOMapper::apply)
				.collect(Collectors.toList());
	}
	
	public List<UserPageRatingDTO> getRatingByPageId(Long id){
		return userPageRatingRepository.findByPageId(id)
				.stream().map(userPageRatingDTOMapper::apply)
				.collect(Collectors.toList());	
	}
	
	public List<UserPageRatingDTO> findAllRating(){
		return userPageRatingRepository.findAllActive()
				.stream().map(userPageRatingDTOMapper::apply)
				.collect(Collectors.toList());	
	}
}