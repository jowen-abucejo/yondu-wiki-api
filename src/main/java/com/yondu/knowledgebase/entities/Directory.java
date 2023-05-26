package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "directories")
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Directory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Directory> subDirectories;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;

    public Directory() {
    }

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public Directory(String name, Directory parent, Set<Directory> subDirectories) {
        this.name = name;
        this.parent = parent;
        this.subDirectories = subDirectories;
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
