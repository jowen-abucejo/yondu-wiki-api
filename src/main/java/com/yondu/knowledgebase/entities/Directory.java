package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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

    @Column(nullable = false, unique = true)
    private String fullPath;

    @ManyToOne(fetch = FetchType.EAGER)
    private Directory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Directory> subDirectories;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryUserAccess> directoryUserAccesses;

    public Directory() {
    }

    // creation of subdirectories
    public Directory(String name, String description, Directory parent) {
        this.name = name;
        this.description = description;
        this.dateCreated = LocalDate.now();
        this.dateModified = LocalDate.now();
        this.fullPath = traverseDirectory(parent) + "/" + this.name;
        this.parent = parent;
        this.subDirectories = new HashSet<>();
        this.directoryUserAccesses = new HashSet<>();
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

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public Set<Directory> getSubDirectories() {
        return subDirectories;
    }

    public void setSubDirectories(Set<Directory> subDirectories) {
        this.subDirectories = subDirectories;
    }


    public Set<DirectoryUserAccess> getDirectoryUserAccesses() {
        Directory current = this;
        Set<DirectoryUserAccess> accesses;
        do {
            accesses = current.directoryUserAccesses;
            current = current.getParent();
        } while ((accesses == null || accesses.isEmpty()) && current != null);
        return accesses;
    }

    public void setDirectoryUserAccesses(Set<DirectoryUserAccess> directoryUserAccesses) {
        this.directoryUserAccesses = directoryUserAccesses;
    }

    public boolean userHasAccess(User user, Permission permission) {
        Set<DirectoryUserAccess> userAccesses = getDirectoryUserAccesses();
        if (userAccesses != null) {
            List<Permission> userDirectoryPermission = userAccesses.stream()
                    .filter(access -> access.getUser().equals(user))
                    .map(DirectoryUserAccess::getPermission)
                    .toList();

            if (!userDirectoryPermission.isEmpty()) {
                return userDirectoryPermission.contains(permission);
            }
        }
        return false;
    }

    public static String traverseDirectory(Directory directory) {
        if (directory.getParent() == null) {
            return directory.getName();
        }
        return traverseDirectory(directory.getParent()) + "/" + directory.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Directory other = (Directory) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }
}
