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

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePagePermission> rolePagePermisisons = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;

    public Role() {
    }

    public Role(Long id, String name, Set<UserPermission> userPermissions) {
        this.id = id;
        this.name = name;
        this.userPermissions = userPermissions;
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
  
  public void setUserPermissions(Set<UserPermission> userPermissions) {
        this.userPermissions = userPermissions;
  }
  
   public Set<RoleDirectoryAccess> getRoleDirectoryAccesses() {
        return roleDirectoryAccesses;
    }

    public void setRoleDirectoryAccesses(Set<RoleDirectoryAccess> roleDirectoryAccesses) {
        this.roleDirectoryAccesses = roleDirectoryAccesses;
    }
  
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userPermissions=" + userPermissions +
                '}';
    }
}
