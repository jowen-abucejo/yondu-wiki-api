package com.yondu.knowledgebase.entities;

import java.util.Set;

import jakarta.persistence.*;

@Entity
public class DirectoryPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private Boolean isDeleted;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserDirectoryAccess> userDirectoryAccesses;

    public DirectoryPermission() {
    }

    public DirectoryPermission(String name, String description) {
        this.name = name;
        this.description = description;
        this.isDeleted = false;
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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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
        return "DirectoryPermission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isDeleted=" + isDeleted +
                ", roleDirectoryAccesses=" + roleDirectoryAccesses +
                ", userDirectoryAccesses=" + userDirectoryAccesses +
                '}';
    }
}
