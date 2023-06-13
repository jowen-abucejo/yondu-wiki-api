package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity(name = "user_page_access")
public class UserPageAccess{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    public UserPageAccess() {
    }

    public UserPageAccess(Long id, Permission permission, User user, Page page) {
        this.id = id;
        this.permission = permission;
        this.user = user;
        this.page = page;
    }

    public UserPageAccess(Permission permission, User user, Page page) {
        this.permission = permission;
        this.user = user;
        this.page = page;
    }

    public Long getId() {
        return id;
    }

    public Permission getPermission() {
        return permission;
    }

    public User getUser() {
        return user;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "UserPageAccess{" +
                "id=" + id +
                ", permission=" + permission +
                ", user=" + user +
                ", page=" + page +
                '}';
    }
}
