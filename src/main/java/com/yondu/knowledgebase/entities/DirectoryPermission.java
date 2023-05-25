package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "directory_permissions")
public class DirectoryPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoleDirectoryPermission> roleDirectoryPermissions;

    public DirectoryPermission() {}

    public DirectoryPermission(String name, String description) {
        this.name = name;
        this.description = description;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<RoleDirectoryPermission> getRoleDirectoryPermissions() {
        return roleDirectoryPermissions;
    }

    public void setRoleDirectoryPermissions(Set<RoleDirectoryPermission> roleDirectoryPermissions) {
        this.roleDirectoryPermissions = roleDirectoryPermissions;
    }

    @Override
    public String toString() {
        return "DirectoryPermission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
