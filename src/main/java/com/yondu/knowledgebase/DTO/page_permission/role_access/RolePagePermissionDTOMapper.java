package com.yondu.knowledgebase.DTO.page_permission.role_access;

import com.yondu.knowledgebase.DTO.page_permission.PageDTOMapper;
import com.yondu.knowledgebase.DTO.page_permission.RoleDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.entities.RolePagePermission;

public class RolePagePermissionDTOMapper {
    public static RolePagePermissionDTO.BaseResponse mapToBaseResponse(RolePagePermission rolePagePermission) {
        return new RolePagePermissionDTO.BaseResponse(rolePagePermission.getId(),
                PageDTOMapper.mapToBaseResponse(rolePagePermission.getPage()),
                PermissionDTOMapper.mapToBaseResponse(rolePagePermission.getPermission()),
                RoleDTOMapper.mapToBaseResponse(rolePagePermission.getRole())
        );
    }
}
