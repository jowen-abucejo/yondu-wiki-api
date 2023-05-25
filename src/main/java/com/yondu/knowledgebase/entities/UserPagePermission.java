package com.yondu.knowledgebase.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_page_permission")
public class UserPagePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private PagePermission pagePermission;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    public UserPagePermission() {
    }

    public UserPagePermission(Long id, PagePermission pagePermission, User user, Page page) {
        this.id = id;
        this.pagePermission = pagePermission;
        this.user = user;
        this.page = page;
    }

    public Long getId() {
        return id;
    }

    public PagePermission getPagePermission() {
        return pagePermission;
    }

    public User getUser() {
        return user;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "UserPagePermission [id=" + id + ", pagePermission=" + pagePermission + ", user=" + user + ", page="
                + page + "]";
    }
    
}
