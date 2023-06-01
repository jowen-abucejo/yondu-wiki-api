package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.DTO.review.ReviewDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PageVersionRepository pageVersionRepository;


    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PageVersionRepository pageVersionRepository) {
        this.reviewRepository = reviewRepository;
        this.pageVersionRepository = pageVersionRepository;
    }

    public List<ReviewDTO.BaseResponse> getAllReviews(){
        return reviewRepository.findAll().stream().map(ReviewDTOMapper::mapToBaseResponse).toList();
    }

    public ReviewDTO.BaseResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review with id " + reviewId + " not found"));

        return ReviewDTOMapper.mapToBaseResponse(review);
    }

    public ReviewDTO.BaseResponse createReview(Long pageId, Long versionId) {
        PageVersion pageVersion = pageVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));

        Review review = new Review();
        review.setPageVersion(pageVersion);
        review.setUser(null);
        review.setComment("");
        review.setReviewDate(null);
        review.setStatus("PENDING");

        pageVersion.getReviews().add(review);

        pageVersionRepository.save(pageVersion);
        reviewRepository.save(review);

        return ReviewDTOMapper.mapToBaseResponse(review);
    }

    public ReviewDTO.UpdatedResponse updateReview(Long id, ReviewDTO.UpdateRequest request) {

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Review id not found: %s", id)));

        if (review.getStatus().toUpperCase().contains("APPROVED") || review.getStatus().toUpperCase().contains("DISAPPROVE")) {
            throw new RequestValidationException("Content is already reviewed.");
        }

        if (request.status().toUpperCase().contains("APPROVED") || request.status().toUpperCase().contains("DISAPPROVE")) {

            review.setUser(currentUser);
            review.setComment(request.comment());
            review.setReviewDate(LocalDate.now());
            review.setStatus(request.status());
            Review updatedReview = reviewRepository.save(review);
            return ReviewDTOMapper.mapToUpdatedResponse(updatedReview);
        } else {
            throw new RequestValidationException("Invalid Status, try APPROVED or DISAPPROVE");
        }

    }

}
