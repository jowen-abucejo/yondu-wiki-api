package com.yondu.knowledgebase.DTO.save;


import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class SaveDTO {



    public record BaseRequest(String entityType, Long entityId) {}

    public record BaseResponse(long id, UserDTO.BaseResponse author, LocalDateTime dateCreated , String entityType, Long entityId) {}

}
