package com.yondu.knowledgebase.DTO.page_permission.role_access;

import com.yondu.knowledgebase.DTO.page_permission.PageDTO;
import com.yondu.knowledgebase.DTO.page_permission.RoleDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;

public class RolePagePermissionDTO {
    public record BaseResponse(Long id, PageDTO.BaseResponse user, PermissionDTO.BaseResponse permission, RoleDTO.BaseResponse role){}
    public record AddRolePermission(Long roleId, Long permissionId) {}
}
