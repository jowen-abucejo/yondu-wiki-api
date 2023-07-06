package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"directory_id", "permission_id", "group_id"})})
public class DirectoryGroupAccess {
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
    @JoinColumn(name="group_id")
    private Group group;

    public DirectoryGroupAccess() {
    }

    public DirectoryGroupAccess(Directory directory, Permission permission, Group group) {
        this.directory = directory;
        this.permission = permission;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
