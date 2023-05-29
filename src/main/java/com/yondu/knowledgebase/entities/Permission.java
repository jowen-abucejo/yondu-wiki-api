package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<DirectoryUserAccess> directoryUserAccesses;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<UserPagePermission> userPagePermission;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<RolePagePermission> rolePagePermissions;

    public Permission() {
    }

    public Permission(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Permission(Long id, String name, String description, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Set<DirectoryUserAccess> getDirectoryUserAccesses() {
        return directoryUserAccesses;
    }

    public Set<UserPagePermission> getUserPagePermission() {
        return userPagePermission;
    }

    public Set<RolePagePermission> getRolePagePermissions() {
        return rolePagePermissions;
    }

    public void setDirectoryUserAccesses(Set<DirectoryUserAccess> directoryUserAccesses) {
        this.directoryUserAccesses = directoryUserAccesses;
    }
}
