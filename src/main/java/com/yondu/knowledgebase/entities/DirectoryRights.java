package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"directory_id", "permission_id"})})
public class DirectoryRights extends Rights{
    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    public DirectoryRights() {}

    public DirectoryRights(Directory directory, Permission permission) {
        this.directory = directory;
        this.permission = permission;
    }

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

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
