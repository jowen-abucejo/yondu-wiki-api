package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<UserPermission> userPermissions = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePagePermission> rolePagePermisisons = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RoleDirectoryAccess> roleDirectoryAccesses;

    public Role() {
    }

    public Role(Long id, String roleName, Set<UserPermission> userPermissions, Set<RolePagePermission> rolePagePermisisons, Set<RoleDirectoryAccess> roleDirectoryAccesses) {
        this.id = id;
        this.roleName = roleName;
        this.userPermissions = userPermissions;
        this.rolePagePermisisons = rolePagePermisisons;
        this.roleDirectoryAccesses = roleDirectoryAccesses;
    }

    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
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

    public Set<RolePagePermission> getRolePagePermisisons() {
        return rolePagePermisisons;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Role other = (Role) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + roleName + '\'' +
                ", userPermissions=" + userPermissions +
                ", rolePagePermisisons=" + rolePagePermisisons +
                ", roleDirectoryAccesses=" + roleDirectoryAccesses +
                '}';
    }
}
