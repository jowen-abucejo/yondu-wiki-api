package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_permission")
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "userPermission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> rolePermissions = new HashSet<>();

    public UserPermission() {
    }

    public UserPermission(Long id, String name, String description, Set<RolePermission> rolePermissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rolePermissions = rolePermissions;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    @Override
    public String toString() {
        return "UserPermission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rolePermissions=" + rolePermissions +
                '}';
    }
}
