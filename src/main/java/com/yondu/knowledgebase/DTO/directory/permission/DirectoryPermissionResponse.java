package com.yondu.knowledgebase.DTO.directory.permission;

import com.yondu.knowledgebase.entities.DirectoryPermission;

public class DirectoryPermissionResponse {
    private Long id;
    private String name;
    private String description;
    private String isDeleted;

    public DirectoryPermissionResponse() {
    }

    public DirectoryPermissionResponse(DirectoryPermission directoryPermission) {
        this.id = directoryPermission.getId();
        this.name = directoryPermission.getName();
        this.description = directoryPermission.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "DirectoryPermissionResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
