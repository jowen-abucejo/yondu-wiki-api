package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<UserPermission> userPermissions = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> user = new HashSet<>();

    public Role() {
    }


    public Role(Long id, String name, Set<UserPermission> userPermissions, Set<User> user) {
        this.id = id;
        this.name = name;
        this.userPermissions = userPermissions;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<UserPermission> getUserPermissions() {
        return userPermissions;
    }

    public Set<User> getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userPermissions=" + userPermissions +
                ", user=" + user +
                '}';
    }
}
