package com.yondu.knowledgebase.DTO.directory.role_access;

import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionResponse;
import com.yondu.knowledgebase.entities.DirectoryRoleAccess;
import com.yondu.knowledgebase.entities.Role;

public class DirectoryRoleAccessResponse {
    private Long id;
    private Long directoryId;
    private Role role;
    private DirectoryResponse directoryResponse;
    private DirectoryPermissionResponse directoryPermissionResponse;

    public DirectoryRoleAccessResponse() {
    }

    public DirectoryRoleAccessResponse(DirectoryRoleAccess directoryRoleAccess) {
        this.id = directoryRoleAccess.getId();
        this.directoryId = directoryRoleAccess.getDirectory().getId();
        this.role = directoryRoleAccess.getRole();
        this.directoryResponse = new DirectoryResponse(directoryRoleAccess.getDirectory());
        this.directoryPermissionResponse = new DirectoryPermissionResponse(directoryRoleAccess.getPermission());
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DirectoryResponse getDirectoryResponse() {
        return directoryResponse;
    }

    public void setDirectoryResponse(DirectoryResponse directoryResponse) {
        this.directoryResponse = directoryResponse;
    }

    public DirectoryPermissionResponse getDirectoryPermissionResponse() {
        return directoryPermissionResponse;
    }

    public void setDirectoryPermissionResponse(DirectoryPermissionResponse directoryPermissionResponse) {
        this.directoryPermissionResponse = directoryPermissionResponse;
    }
}
