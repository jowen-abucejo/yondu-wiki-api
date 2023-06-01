package com.yondu.knowledgebase.DTO.page_permission.user_access;

import com.yondu.knowledgebase.DTO.page_permission.PageDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class UserPagePermissionDTO {
    public record BaseResponse(Long id, UserDTO.BaseResponse user, PermissionDTO.BaseResponse permission, PageDTO.BaseResponse page, Boolean isActive, String dateCreated, String lastModified){}
    public record AddUser(Set<UserPagePermissionDTO.UserPermissionPair> userPermissionPair) {}
    public record UserPermissionPair(Long userId, Long permissionId){}
}
