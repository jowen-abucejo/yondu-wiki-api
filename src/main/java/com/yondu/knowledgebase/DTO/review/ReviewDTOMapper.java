package com.yondu.knowledgebase.DTO.review;


import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Review;

public class ReviewDTOMapper {

    public static ReviewDTO.BaseResponse mapToBaseResponse(Review review) {
        return new ReviewDTO.BaseResponse(
                review.getId(),
                review.getComment(),
                review.getReviewDate(),
   //             UserDTOMapper.mapToGeneralResponse(review.getUser()),
                review.getComment());
    }
}
