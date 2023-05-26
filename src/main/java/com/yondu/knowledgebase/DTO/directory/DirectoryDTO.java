package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.RoleDirectoryAccess;
import com.yondu.knowledgebase.entities.UserDirectoryAccess;

import java.util.Set;

public class DirectoryDTO {
    private Long id;
    private String name;
    private Directory parent;
    private Set<Directory> subDirectories;
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;
    private Set<UserDirectoryAccess> userDirectoryAccesses;

    public DirectoryDTO() {
    }

    public DirectoryDTO(Directory directory) {
        this.id = directory.getId();
        this.name = directory.getName();
        this.parent = directory.getParent();
        this.subDirectories = directory.getSubDirectories();
        this.roleDirectoryAccesses = directory.getRoleDirectoryAccesses();
        this.userDirectoryAccesses = directory.getUserDirectoryAccesses();
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

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public Set<Directory> getSubDirectories() {
        return subDirectories;
    }

    public void setSubDirectories(Set<Directory> subDirectories) {
        this.subDirectories = subDirectories;
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
        return "DirectoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", subDirectories=" + subDirectories +
                ", roleDirectoryAccesses=" + roleDirectoryAccesses +
                ", userDirectoryAccesses=" + userDirectoryAccesses +
                '}';
    }
}
