package com.yondu.knowledgebase.DTO.directory.role_access;

import com.yondu.knowledgebase.entities.DirectoryRoleAccess;

public class DirectoryRoleAccessRequest {
    private Long roleId;
    private Long permissionId;

    public DirectoryRoleAccessRequest() {
    }

    public DirectoryRoleAccessRequest(DirectoryRoleAccess directoryRoleAccess) {
        this.roleId = directoryRoleAccess.getRole().getId() ;
        this.permissionId = directoryRoleAccess.getPermission().getId();
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public String toString() {
        return "DirectoryRoleAccessRequest{" +
                "roleId=" + roleId +
                ", permissionId=" + permissionId +
                '}';
    }
}
