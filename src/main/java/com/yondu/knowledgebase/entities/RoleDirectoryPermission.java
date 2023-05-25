package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role_directory_permissions")
public class RoleDirectoryPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private Directory directory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_permission_id")
    private DirectoryPermission permission;

    public RoleDirectoryPermission() {}

    public RoleDirectoryPermission(Role role, Directory directory, DirectoryPermission permission) {
        this.role = role;
        this.directory = directory;
        this.permission = permission;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public DirectoryPermission getPermission() {
        return permission;
    }

    public void setPermission(DirectoryPermission permission) {
        this.permission = permission;
    }


}
