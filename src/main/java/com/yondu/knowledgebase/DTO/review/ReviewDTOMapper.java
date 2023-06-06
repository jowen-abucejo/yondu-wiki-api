package com.yondu.knowledgebase.DTO.review;


import com.yondu.knowledgebase.DTO.review.pageVersion.PageVerDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Review;

public class ReviewDTOMapper {

    public static ReviewDTO.BaseResponse mapToBaseResponse(Review review) {
        return new ReviewDTO.BaseResponse(
                review.getId(),
                PageVerDTOMapper.mapToBaseResponse(review.getPageVersion()),
                review.getComment(),
                review.getReviewDate(),
                review.getStatus());
    }

    public static ReviewDTO.UpdatedResponse mapToUpdatedResponse(Review review) {
        return new ReviewDTO.UpdatedResponse(
                review.getId(),
                PageVerDTOMapper.mapToBaseResponse(review.getPageVersion()),
                review.getComment(),
                review.getReviewDate(),
                UserDTOMapper.mapToGeneralResponse(review.getUser()),
                review.getStatus());
    }


}
