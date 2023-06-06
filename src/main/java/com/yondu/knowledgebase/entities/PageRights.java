package com.yondu.knowledgebase.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PageRights extends Rights{
    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    public PageRights() {
    }

    public PageRights(Page page, Permission permission) {
        this.page = page;
        this.permission = permission;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
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
