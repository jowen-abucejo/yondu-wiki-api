package com.yondu.knowledgebase.DTO.directory.role_access;

import com.yondu.knowledgebase.entities.DirectoryRoleAccess;

public class DirectoryRoleAccessResponse {
    private Long id;
    private Long directoryId;
    private String directoryName;
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionName;

    public DirectoryRoleAccessResponse() {
    }

    public DirectoryRoleAccessResponse(DirectoryRoleAccess directoryRoleAccess) {
        this.id = directoryRoleAccess.getId();
        this.directoryId = directoryRoleAccess.getDirectory().getId();
        this.directoryName = directoryRoleAccess.getDirectory().getName();
        this.roleId = directoryRoleAccess.getRole().getId();
        this.roleName = directoryRoleAccess.getRole().getRoleName();
        this.permissionId = directoryRoleAccess.getPermission().getId();
        this.permissionName = directoryRoleAccess.getPermission().getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String toString() {
        return "DirectoryRoleAccessResponse{" +
                "id=" + id +
                ", directoryId=" + directoryId +
                ", directoryName='" + directoryName + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", permissionId=" + permissionId +
                ", permissionName='" + permissionName + '\'' +
                '}';
    }
}
