package com.yondu.knowledgebase.DTO.review;

import com.yondu.knowledgebase.DTO.review.pageVersion.PageVerDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.entities.Review;

import java.time.LocalDateTime;

public class ReviewDTO {

    public record BaseResponse(Long id,
                               PageVerDTO.BaseResponse version,
                               String comment,
                               LocalDateTime reviewDate,
                               String status){}

    public record UpdatedResponse(Long id, PageVerDTO.BaseResponse version,
                                  String comment,
                                  LocalDateTime reviewDate, UserDTO.GeneralResponse user,
                                  String status){}

    public record UpdateRequest(String comment, String status) {}

    public record ApproverResponse(Long id,
                               PageVerDTO.BaseResponse version,
                               String comment,
                               LocalDateTime reviewDate,UserDTO.ApproverResponse user,
                               String status){}

    public record CanApproveResponse (Long id,
                                      Long pageId,
                                      Long versionId,
                                      UserDTO.ApproverResponse user,
                                      boolean canApprove){}

}
