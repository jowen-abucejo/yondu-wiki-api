package com.yondu.knowledgebase.DTO.page_rights;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class PageDTO {
    public record BaseResponse(Long id, LocalDateTime dateCreated, UserDTO.ShortResponse author, Boolean active,
                               Boolean deleted){}

}
