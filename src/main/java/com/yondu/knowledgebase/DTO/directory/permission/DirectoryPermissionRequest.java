package com.yondu.knowledgebase.DTO.directory.permission;

import com.yondu.knowledgebase.entities.DirectoryPermission;

public class DirectoryPermissionRequest {
    private String name;
    private String description;

    public DirectoryPermissionRequest() {
    }

    public DirectoryPermissionRequest(DirectoryPermission directoryPermission) {
        this.name = directoryPermission.getName();
        this.description = directoryPermission.getDescription();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
