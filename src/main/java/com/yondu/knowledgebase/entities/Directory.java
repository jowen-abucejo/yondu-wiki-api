package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "parent_id"})})
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Directory parent;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Directory> subDirectories;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserDirectoryAccess> userDirectoryAccesses;

    public Directory() {
    }

    // creation of root
    public Directory(String name) {
        this.name = name;
        this.parent = null;
        this.isDeleted = false;
        this.subDirectories = new HashSet<>();
        this.roleDirectoryAccesses = new HashSet<>();
        this.userDirectoryAccesses = new HashSet<>();
    }

    // creation of subdirectories
    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
        this.isDeleted = false;
        this.subDirectories = new HashSet<>();
        this.roleDirectoryAccesses = new HashSet<>();
        this.userDirectoryAccesses = new HashSet<>();
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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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
        Directory currentDirectory = this.parent;
        StringBuilder directory = new StringBuilder(this.name);
        while(currentDirectory != null) {
            directory.insert(0, currentDirectory.name + "/");
            currentDirectory = currentDirectory.parent;
        }
        return directory.toString();
    }
}
