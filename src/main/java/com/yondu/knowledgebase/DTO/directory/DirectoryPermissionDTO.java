package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.entities.DirectoryPermission;
import com.yondu.knowledgebase.entities.RoleDirectoryAccess;
import com.yondu.knowledgebase.entities.UserDirectoryAccess;

import java.util.Set;

public class DirectoryPermissionDTO {
    private Long id;
    private String name;
    private String description;
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;
    private Set<UserDirectoryAccess> userDirectoryAccesses;

    public DirectoryPermissionDTO() {
    }

    public DirectoryPermissionDTO(DirectoryPermission directoryPermission) {
        this.id = directoryPermission.getId();
        this.name = directoryPermission.getName();
        this.description = directoryPermission.getDescription();
        this.roleDirectoryAccesses = directoryPermission.getRoleDirectoryAccesses();
        this.userDirectoryAccesses = directoryPermission.getUserDirectoryAccesses();
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

    public Set<RoleDirectoryAccess> getRoleDirectoryAccesses() {
        return roleDirectoryAccesses;
    }

    public void setRoleDirectoryAccesses(Set<RoleDirectoryAccess> roleDirectoryAccesses) {
        this.roleDirectoryAccesses = roleDirectoryAccesses;
    }

    public Set<UserDirectoryAccess> getUserDirectoryAccesses() {
        return userDirectoryAccesses;
    }

    public void setUserDirectoryAccesses(Set<UserDirectoryAccess> userDirectoryAccesses) {
        this.userDirectoryAccesses = userDirectoryAccesses;
    }

    @Override
    public String toString() {
        return "DirectoryPermissionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", roleDirectoryAccesses=" + roleDirectoryAccesses +
                ", userDirectoryAccesses=" + userDirectoryAccesses +
                '}';
    }
}
