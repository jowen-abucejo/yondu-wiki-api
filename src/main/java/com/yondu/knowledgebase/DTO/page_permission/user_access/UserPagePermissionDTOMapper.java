package com.yondu.knowledgebase.DTO.page_permission.user_access;

import com.yondu.knowledgebase.DTO.page_permission.PageDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.UserPagePermission;

public class UserPagePermissionDTOMapper {
    public static UserPagePermissionDTO.BaseResponse mapToBaseResponse(UserPagePermission userPagePermission) {
        return new UserPagePermissionDTO.BaseResponse(userPagePermission.getId(),
                UserDTOMapper.mapToBaseResponse(userPagePermission.getUser()),
                PermissionDTOMapper.mapToBaseResponse(userPagePermission.getPermission()),
                PageDTOMapper.mapToBaseResponse(userPagePermission.getPage()),
                userPagePermission.getActive(), userPagePermission.getDateCreated().toString(),
                userPagePermission.getLastModified().toString());
    }

}
