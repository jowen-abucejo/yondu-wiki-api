package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.DTO.review.ReviewDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PageVersionRepository pageVersionRepository;


    @Autowired
    public ReviewService(ReviewRepository reviewRepository, PageVersionRepository pageVersionRepository) {
        this.reviewRepository = reviewRepository;
        this.pageVersionRepository = pageVersionRepository;
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviews( int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAll(pageRequest);
        List<Review> reviews = reviewPages.getContent();

        List<ReviewDTO.BaseResponse> review = reviews.stream()
                .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(review, page,size, (long)review.size());
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviewsByStatus(String status, int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAllByStatus(status, pageRequest);
        List<Review> reviews = reviewPages.getContent();

        if(reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found");
        }
            List<ReviewDTO.BaseResponse> review = reviews.stream()
                    .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                    .collect(Collectors.toList());

        return new PaginatedResponse<>(review, page,size, (long)review.size());
    }

    public PaginatedResponse<ReviewDTO.BaseResponse> getAllReviewsByPageTitle(String title, int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Review> reviewPages = reviewRepository.findAllByPageVersionTitle("%" + title.toLowerCase() + "%", pageRequest);
        List<Review> reviews = reviewPages.getContent();

        if(reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found");
        }
        List<ReviewDTO.BaseResponse> review = reviews.stream()
                .map(rev -> ReviewDTOMapper.mapToBaseResponse(rev))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(review, page,size, (long)review.size());
    }

    public ReviewDTO.BaseResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review with id " + reviewId + " not found"));

        return ReviewDTOMapper.mapToBaseResponse(review);
    }

    public ReviewDTO.BaseResponse createReview(Long pageId, Long versionId) {
        PageVersion pageVersion = pageVersionRepository.findByPageIdAndId(pageId,versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Page version not found"));

        if (pageVersion.getReviews().isEmpty()) {
            Review review = new Review();
            review.setPageVersion(pageVersion);
            review.setUser(null);
            review.setComment("");
            review.setReviewDate(null);
            review.setStatus("PENDING");

            reviewRepository.save(review);

            return ReviewDTOMapper.mapToBaseResponse(review);
        } else {
            throw new RequestValidationException("Content already submitted, submit the latest version instead.");
        }


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
