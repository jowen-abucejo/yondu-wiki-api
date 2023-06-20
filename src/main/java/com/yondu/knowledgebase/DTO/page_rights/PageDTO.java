package com.yondu.knowledgebase.DTO.page_rights;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class  PageDTO {
    public record BaseResponse(Long id, LocalDateTime created_at, UserDTO.ShortResponse author, Boolean not_Archived,
                               Boolean commenting_on){}

}
