package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "cluster")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    @ManyToOne(fetch = FetchType.EAGER)
    private User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    private User modifiedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_users", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @ManyToMany
    @JoinTable(name = "group_permissions", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GroupPageAccess> groupPageAccess = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DirectoryGroupAccess> directoryGroupAccesses = new HashSet<>();

    public Group() {
    }

    public Group(String name, String description, User createdBy) {
        this.name = name;
        this.description = description;
        this.isActive = true;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
        this.users = new HashSet<>();
        // this.rights = new HashSet<>();
        this.groupPageAccess = new HashSet<>();
        this.permissions = new HashSet<>();
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<GroupPageAccess> getGroupPageAccess() {
        return groupPageAccess;
    }

    public void setGroupPageAccess(Set<GroupPageAccess> groupPageAccess) {
        this.groupPageAccess = groupPageAccess;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
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
        Group other = (Group) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }
}
