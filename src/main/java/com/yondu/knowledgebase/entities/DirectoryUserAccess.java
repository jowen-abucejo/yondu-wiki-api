package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
public class DirectoryUserAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="directory_id")
    private Directory directory;
    @ManyToOne
    @JoinColumn(name="permission_id")
    private Permission permission;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
