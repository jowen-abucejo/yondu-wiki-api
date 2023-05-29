package com.yondu.knowledgebase.DTO.page_permission.user_access;

import com.yondu.knowledgebase.DTO.page_permission.PageDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

public class UserPagePermissionDTO {
    public record BaseResponse(Long id, UserDTO.BaseResponse user, PermissionDTO.BaseResponse permission, PageDTO.BaseResponse page, Boolean isActive, String dateCreated, String lastModified){

    }
    public record AddUser(Long userId, Long pageId) {}
}
