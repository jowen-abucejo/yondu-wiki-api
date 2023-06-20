package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity(name = "group_page_access")
public class GroupPageAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    public GroupPageAccess() {
    }

    public GroupPageAccess(Long id, Permission permission, Group group, Page page) {
        this.id = id;
        this.permission = permission;
        this.group = group;
        this.page = page;
    }

    public GroupPageAccess(Permission permission, Group group, Page page) {
        this.permission = permission;
        this.group = group;
        this.page = page;
    }

    public Long getId() {
        return id;
    }

    public Permission getPermission() {
        return permission;
    }

    public Group getGroup() {
        return group;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "UserPageAccess{" +
                "id=" + id +
                ", permission=" + permission +
                ", group=" + group +
                ", page=" + page +
                '}';
    }
}
