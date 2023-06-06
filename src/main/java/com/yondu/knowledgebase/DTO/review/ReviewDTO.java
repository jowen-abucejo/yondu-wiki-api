package com.yondu.knowledgebase.DTO.review;

import com.yondu.knowledgebase.DTO.review.pageVersion.PageVerDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.entities.Review;

import java.time.LocalDate;

public class ReviewDTO {

    public record BaseResponse(Long id,
                               PageVerDTO.BaseResponse version,
                               String comment,
                               LocalDate reviewDate,
                               Review.Status status){}

    public record UpdatedResponse(Long id, PageVerDTO.BaseResponse version,
                                  String comment,
                                  LocalDate reviewDate, UserDTO.GeneralResponse user,
                                  Review.Status status){}

    public record UpdateRequest(String comment, String status) {}
}
