package com.yondu.knowledgebase.DTO.review;


import com.yondu.knowledgebase.DTO.review.pageVersion.PageVerDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Review;
import com.yondu.knowledgebase.entities.User;

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

    public static ReviewDTO.ApproverResponse mapToApproverResponse(Review review) {
        return new ReviewDTO.ApproverResponse(
                review.getId(),
                PageVerDTOMapper.mapToBaseResponse(review.getPageVersion()),
                review.getComment(),
                review.getReviewDate(),
                UserDTOMapper.mapToApproverResponse(review.getUser()),
                review.getStatus());

    }
    public static ReviewDTO.CanApproveResponse mapToCanApproveResponse(Review review, User currentUser, boolean canApprove) {
        return new ReviewDTO.CanApproveResponse(
                review.getId(),
                review.getPageVersion().getPage().getId(),
                review.getPageVersion().getId(),
                UserDTOMapper.mapToApproverResponse(currentUser),
                canApprove);
    }
}
