package com.yondu.knowledgebase.DTO.review.pageVersion;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class PageDTOr {

    public record BaseResponse(Long id, LocalDateTime dateCreated, UserDTO.BaseResponse author, Boolean active, String type){}
}
