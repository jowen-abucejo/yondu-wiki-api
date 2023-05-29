package com.yondu.knowledgebase.DTO.page_permission;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class PageDTO {
    public record BaseResponse(Long id, LocalDateTime dateCreated, UserDTO.BaseResponse author, Boolean active,
                               Boolean deleted){}

}
