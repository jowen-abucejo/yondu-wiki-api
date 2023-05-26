package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role_directory_accesses")
public class RoleDirectoryAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private Directory directory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_permission_id")
    private DirectoryPermission permission;

    public RoleDirectoryAccess() {}

    public RoleDirectoryAccess(Role role, Directory directory, DirectoryPermission permission) {
        this.role = role;
        this.directory = directory;
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "RoleDirectoryAccess{" +
                "id=" + id +
                ", role=" + role +
                ", directory=" + directory +
                ", permission=" + permission +
                '}';
    }
}
