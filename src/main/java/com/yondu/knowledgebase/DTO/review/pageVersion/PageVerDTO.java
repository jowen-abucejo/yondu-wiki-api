package com.yondu.knowledgebase.DTO.review.pageVersion;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class PageVerDTO {

    public record BaseResponse(Long id, String title, String content,PageDTOr.BaseResponse page, LocalDateTime dateModified, UserDTO.BaseResponse modifiedBy){}

}
