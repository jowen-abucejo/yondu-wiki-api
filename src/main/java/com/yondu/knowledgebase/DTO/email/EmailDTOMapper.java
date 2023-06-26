package com.yondu.knowledgebase.DTO.email;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.User;

import java.time.LocalDateTime;

public class EmailDTOMapper {
    public static EmailDTO.BaseResponse generalRequestToBaseResponse (EmailDTO.GeneralRequest request, User toUser, User fromUser){
        return new EmailDTO.BaseResponse(
                UserDTOMapper.mapToShortResponse(toUser),
                UserDTOMapper.mapToShortResponse(fromUser),
                request.notificationType(),
                LocalDateTime.now()
        );
    }

    public static EmailDTO.BaseResponse newUserRequestToBaseResponse (EmailDTO.NewUserRequest request, User toUser, User fromUser){
        return new EmailDTO.BaseResponse(
                UserDTOMapper.mapToShortResponse(toUser),
                UserDTOMapper.mapToShortResponse(fromUser),
                request.notificationType(),
                LocalDateTime.now()
        );
    }

}
