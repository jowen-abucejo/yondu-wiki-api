package com.yondu.knowledgebase.DTO.role;

import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.entities.Role;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleDTO {

    private Long id;
    private String roleName;
    private Set<PermissionDTO.BaseResponse> permission;

    public RoleDTO(Role role) {
        this.id = role.getId();
        this.roleName = role.getRoleName();
        this.permission = role.getUserPermissions().stream()
                .map(PermissionDTOMapper::mapToBaseResponse)
                .collect(Collectors.toSet());
    }

    public RoleDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<PermissionDTO.BaseResponse> getPermission() {
        return permission;
    }

    public void setPermission(Set<PermissionDTO.BaseResponse> permission) {
        this.permission = permission;
    }


}
