package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role_permission")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private UserPermission userPermission;

    public RolePermission() {
    }

    public RolePermission(Long id, Role role, UserPermission userPermission) {
        this.id = id;
        this.role = role;
        this.userPermission = userPermission;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public UserPermission getUserPermission() {
        return userPermission;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", role=" + role +
                ", userPermission=" + userPermission +
                '}';
    }
}
