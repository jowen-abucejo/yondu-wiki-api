package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
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
    private Set<UserPageAccess> userPageAccess = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<GroupPageAccess> groupPageAccess = new HashSet<>();

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryUserAccess> directoryUserAccesses = new HashSet<>();

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryGroupAccess> directoryGroupAccesses = new HashSet<>();

    public Permission() {
    }

    public Permission(Long id) {
        this.id = id;
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

    public Set<UserPageAccess> getUserPageAccess() {
        return userPageAccess;
    }

    public Set<GroupPageAccess> getGroupPageAccess() {
        return groupPageAccess;
    }

    public Set<DirectoryUserAccess> getDirectoryUserAccesses() {
        return directoryUserAccesses;
    }

    public void setDirectoryUserAccesses(Set<DirectoryUserAccess> directoryUserAccesses) {
        this.directoryUserAccesses = directoryUserAccesses;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUserPageAccess(Set<UserPageAccess> userPageAccess) {
        this.userPageAccess = userPageAccess;
    }

    public void setGroupPageAccess(Set<GroupPageAccess> groupPageAccess) {
        this.groupPageAccess = groupPageAccess;
    }

    public Set<DirectoryGroupAccess> getDirectoryGroupAccesses() {
        return directoryGroupAccesses;
    }

    public void setDirectoryGroupAccesses(Set<DirectoryGroupAccess> directoryGroupAccesses) {
        this.directoryGroupAccesses = directoryGroupAccesses;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Permission other = (Permission) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }

}
