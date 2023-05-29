package com.yondu.knowledgebase.DTO.directory.user_access;

import com.yondu.knowledgebase.entities.DirectoryUserAccess;

public class DirectoryUserAccessRequest {
    private Long userId;
    private Long permissionId;

    public DirectoryUserAccessRequest() {
    }

    public DirectoryUserAccessRequest(DirectoryUserAccess directoryUserAccess) {
        this.userId = directoryUserAccess.getUser().getId() ;
        this.permissionId = directoryUserAccess.getPermission().getId();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
