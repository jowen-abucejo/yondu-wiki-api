package com.yondu.knowledgebase.DTO.page_permission;

import com.yondu.knowledgebase.entities.Role;

public class RoleDTOMapper {

    public static RoleDTO.BaseResponse mapToBaseResponse(Role role) {
        return new RoleDTO.BaseResponse(role.getId(), role.getRoleName());
    }

}
