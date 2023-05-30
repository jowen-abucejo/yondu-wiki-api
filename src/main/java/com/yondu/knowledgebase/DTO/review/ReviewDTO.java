package com.yondu.knowledgebase.DTO.review;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDate;

public class ReviewDTO {

    public record BaseResponse(Long id,
                               String comment,
                               LocalDate reviewDate,
                               String status){}

    public record UpdatedResponse(Long id,
                               String comment,
                               LocalDate reviewDate, UserDTO.GeneralResponse user,
                               String status){}

    public record UpdateRequest(String comment, String status) {}
}
