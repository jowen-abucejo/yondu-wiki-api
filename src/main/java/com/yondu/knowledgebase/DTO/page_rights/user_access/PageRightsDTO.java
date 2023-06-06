package com.yondu.knowledgebase.DTO.page_rights.user_access;

import com.yondu.knowledgebase.DTO.page_rights.PageDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class PageRightsDTO {
    public record BaseResponse(Long id, UserDTO.ShortResponse user, PermissionDTO.BaseResponse permission, PageDTO.BaseResponse page){}
    public record AddUser(Set<PageRightsDTO.UserPermissionPair> userPermissionPair) {}
    public record UserPermissionPair(Long userId, Long permissionId){}



    // Page Rights
    public record PageRightBaseResponse(Long id, PermissionDTO.BaseResponse permission, PageDTO.BaseResponse page){}
}
