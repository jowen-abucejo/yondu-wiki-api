package com.yondu.knowledgebase.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class DirectoryRoleAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
//    @JsonBackReference
    @JoinColumn(name = "directory_id")
    private Directory directory;

    @ManyToOne(fetch = FetchType.EAGER)
//    @JsonBackReference
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
//    @JsonBackReference
    @JoinColumn(name = "directory_permission_id")
    private DirectoryPermission permission;

    public DirectoryRoleAccess() {
    }

    public DirectoryRoleAccess(Directory directory, Role role, DirectoryPermission permission) {
        this.directory = directory;
        this.role = role;
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DirectoryRoleAccess other = (DirectoryRoleAccess) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }
}
