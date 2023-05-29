package com.yondu.knowledgebase.DTO.user;

import com.yondu.knowledgebase.entities.User;

public class UserDTOMapper {
    public UserDTO.BaseResponse mapToBaseResponse(User user) {
        return new UserDTO.BaseResponse(user.getId(), user.getEmail());
    }
}
