package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
public class DirectoryUserAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private Directory directory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_permission_id")
    private DirectoryPermission permission;

    public DirectoryUserAccess() {
    }

    public DirectoryUserAccess(User user, Directory directory, DirectoryPermission permission) {
        this.user = user;
        this.directory = directory;
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        DirectoryUserAccess other = (DirectoryUserAccess) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }
}
