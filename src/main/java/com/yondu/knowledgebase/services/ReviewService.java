package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.entities.Review;
import com.yondu.knowledgebase.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PageVersionRepository pageVersionRepository;
    private final UserService userService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PageVersionRepository pageVersionRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.userService = userService;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewById(Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        return optionalReview.orElse(null);
    }

    public Review createReview(Long pageId, Long versionId) {
        PageVersion pageVersion = pageVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));

        Review review = new Review();
        review.setPageVersion(pageVersion);
        review.setUser(null);
        review.setComment(null);
        review.setReviewDate(null);
        review.setStatus("PENDING");

        pageVersion.getReviews().add(review);

        pageVersionRepository.save(pageVersion);

        return reviewRepository.save(review);
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
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

}
