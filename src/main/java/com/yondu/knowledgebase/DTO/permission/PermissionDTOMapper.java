package com.yondu.knowledgebase.DTO.permission;

import com.yondu.knowledgebase.entities.Permission;

public class PermissionDTOMapper {
    public PermissionDTO.BaseResponse mapToBaseResponse(Permission permission) {
        return new PermissionDTO.BaseResponse(permission.getId(), permission.getName(), permission.getDescription(), permission.getCategory());
    }
}
