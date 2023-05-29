package com.yondu.knowledgebase.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "role_page_permission")
public class RolePagePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    public RolePagePermission() {
    }

    public RolePagePermission(Long id, Permission permission, Role role, Page page) {
        this.id = id;
        this.permission = permission;
        this.role = role;
        this.page = page;
    }

    public Long getId() {
        return id;
    }

    public Permission getPermission() {
        return permission;
    }

    public Role getRole() {
        return role;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "RolePagePermission [id=" + id + ", permission=" + permission + ", role=" + role + ", page="
                + page + "]";
    }

}
