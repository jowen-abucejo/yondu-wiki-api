package com.yondu.knowledgebase.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "page_permisison")
public class PagePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "pagePermission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPagePermission> userPagePermisison = new HashSet<>();

    @OneToMany(mappedBy = "pagePermission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePagePermission> rolePagePermisison = new HashSet<>();
    
    public PagePermission() {
    }

    public PagePermission(Long id, String name, String description, Set<UserPagePermission> userPagePermisison,
            Set<RolePagePermission> rolePagePermisison) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userPagePermisison = userPagePermisison;
        this.rolePagePermisison = rolePagePermisison;
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

    public Set<UserPagePermission> getUserPagePermisison() {
        return userPagePermisison;
    }

    public Set<RolePagePermission> getRolePagePermisison() {
        return rolePagePermisison;
    }

    @Override
    public String toString() {
        return "PagePermission [id=" + id + ", name=" + name + ", description=" + description + ", userPagePermisison="
                + userPagePermisison + ", rolePagePermisison=" + rolePagePermisison + "]";
    }

    

}
