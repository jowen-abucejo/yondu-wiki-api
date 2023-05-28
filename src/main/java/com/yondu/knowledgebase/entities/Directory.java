package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
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

    private String description;

    @Column(nullable = false)
    private LocalDate dateCreated;

    @Column(nullable = false)
    private LocalDate dateModified;

    @ManyToOne(fetch = FetchType.LAZY)
    private Directory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Directory> subDirectories;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserDirectoryAccess> userDirectoryAccesses;

    public Directory() {
    }

    // creation of root
    public Directory(String name, String description) {
        this.name = name;
        this.description = description;
        this.parent = null;
        this.subDirectories = new HashSet<>();
        this.roleDirectoryAccesses = new HashSet<>();
        this.userDirectoryAccesses = new HashSet<>();
    }

    // creation of subdirectories
    public Directory(String name, String description, Directory parent) {
        this.name = name;
        this.description = description;
        this.dateCreated = LocalDate.now();
        this.dateModified = LocalDate.now();
        this.parent = parent;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
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
}
