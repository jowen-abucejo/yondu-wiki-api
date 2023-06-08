package com.yondu.knowledgebase.DTO.page_rights.user_access;

import com.yondu.knowledgebase.DTO.page_rights.PageDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class  UserRightsDTO {
    public record BaseResponse(Long id, UserDTO.BaseResponse user, PermissionDTO.BaseResponse permission, PageDTO.BaseResponse page, Boolean isActive, String dateCreated, String lastModified){}
    public record AddUser(Set<UserRightsDTO.UserPermissionPair> userPermissionPair) {}
    public record UserPermissionPair(Long userId, Long permissionId){}



    // Page Rights
    public record AddPageRightRequest (Long permissionId){}
}
