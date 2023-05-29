package com.yondu.knowledgebase.DTO.directory.user_access;

public class DirectoryUserAccessRequest {
    private Long userId;
    private Long permissionId;

    public DirectoryUserAccessRequest() {
    }

    public DirectoryUserAccessRequest(Long userId, Long permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
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

    @Override
    public String toString() {
        return "DirectoryUserAccessRequest{" +
                "userId=" + userId +
                ", permissionId=" + permissionId +
                '}';
    }
}
