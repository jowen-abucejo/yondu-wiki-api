package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.DTO.review.ReviewDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PageVersionRepository pageVersionRepository;
    private final UserService userService;
    private  final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PageVersionRepository pageVersionRepository, UserService userService, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

//    public List<Review> getAllReviews() {
//        return reviewRepository.findAll();
//    }

    public List<ReviewDTO.BaseResponse> getAllReviews(){
        return reviewRepository.findAll().stream().map(ReviewDTOMapper::mapToBaseResponse).toList();
    }

    public ReviewDTO.BaseResponse createReview(Long pageId, Long versionId) {
        PageVersion pageVersion = pageVersionRepository.findById(versionId)
                .orElseThrow(() -> new NotFoundException("Page version not found"));

        Review review = new Review();
        review.setPageVersion(pageVersion);
        review.setUser(null);
        review.setComment(null);
        review.setReviewDate(null);
        review.setStatus("PENDING");

        pageVersion.getReviews().add(review);

        pageVersionRepository.save(pageVersion);
        reviewRepository.save(review);

        return ReviewDTOMapper.mapToBaseResponse(review);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        // Load the User entity based on the email
        User user = userService.loadUserByUsername(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        return user;
    }

}
