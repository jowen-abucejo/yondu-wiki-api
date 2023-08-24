package com.yondu.knowledgebase.DTO.role;

import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.entities.Role;

import java.util.stream.Collectors;

public class RoleDTOMapper {

    public static RoleDTO.PaginatedResponse mapToPaginatedResponse(Role role, Boolean isEmpty) {

        return new RoleDTO.PaginatedResponse(
                role.getId(),
                role.getRoleName(),
                role.getUserPermissions().stream()
                        .map(PermissionDTOMapper::mapToBaseResponse)
                        .collect(Collectors.toSet()),
                isEmpty);
    }
}
