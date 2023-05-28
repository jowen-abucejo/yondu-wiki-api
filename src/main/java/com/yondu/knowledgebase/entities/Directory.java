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

    @ManyToOne(fetch = FetchType.LAZY)
    private Directory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Directory> subDirectories;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryRoleAccess> directoryRoleAccesses;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryUserAccess> directoryUserAccesses;

    public Directory() {
    }

    // creation of root
    public Directory(String name, String description) {
        this.name = name;
        this.description = description;
        this.parent = null;
        this.subDirectories = new HashSet<>();
        this.directoryRoleAccesses = new HashSet<>();
        this.directoryUserAccesses = new HashSet<>();
    }

    // creation of subdirectories
    public Directory(String name, String description, Directory parent) {
        this.name = name;
        this.description = description;
        this.dateCreated = LocalDate.now();
        this.dateModified = LocalDate.now();
        this.parent = parent;
        this.subDirectories = new HashSet<>();
        this.directoryRoleAccesses = new HashSet<>();
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

    public Set<Directory> getSubDirectories() {
        return subDirectories;
    }

    public void setSubDirectories(Set<Directory> subDirectories) {
        this.subDirectories = subDirectories;
    }

    public Set<DirectoryRoleAccess> getDirectoryRoleAccesses() {
        Set<DirectoryRoleAccess> rda = this.directoryRoleAccesses;
        Directory current = this;
        while (rda == null || rda.isEmpty()) {
            rda = current.getParent().directoryRoleAccesses;
            current = current.getParent();
        }
        return rda;
    }

    public void setDirectoryRoleAccesses(Set<DirectoryRoleAccess> directoryRoleAccesses) {
        this.directoryRoleAccesses = directoryRoleAccesses;
    }

    public Set<DirectoryUserAccess> getDirectoryUserAccesses() {
        Directory current = this;
        Set<DirectoryUserAccess> uda;
        do {
            uda = current.directoryUserAccesses;
            current = current.getParent();
        } while ((uda == null || uda.isEmpty()) && current != null);
        return uda;
    }

    public void setDirectoryUserAccesses(Set<DirectoryUserAccess> directoryUserAccesses) {
        this.directoryUserAccesses = directoryUserAccesses;
    }

    public boolean userHasAccess(User user, DirectoryPermission permission) {
        Set<DirectoryUserAccess> directoryUserAccesses = getDirectoryUserAccesses();
        if (directoryUserAccesses != null) {
            List<DirectoryPermission> userDirectoryPermission = directoryUserAccesses.stream()
                    .filter(uda -> uda.getUser().equals(user))
                    .map(DirectoryUserAccess::getPermission)
                    .toList();

            if (!userDirectoryPermission.isEmpty()) {
                return userDirectoryPermission.contains(permission);
            }
        }

        Set<DirectoryRoleAccess> directoryRoleAccesses = getDirectoryRoleAccesses();
        if (directoryRoleAccesses != null) {
            return user.getRole().stream()
                    .anyMatch(role ->
                            directoryRoleAccesses.stream()
                                    .anyMatch(rda -> rda.getRole().equals(role) && rda.getPermission().equals(permission)));
        }

        return false;
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
