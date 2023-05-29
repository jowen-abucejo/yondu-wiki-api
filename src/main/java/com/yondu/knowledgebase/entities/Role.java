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
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePagePermission> rolePagePermisisons = new HashSet<>();

    public Role() {
    }

    public Role(Long id, String roleName, Set<Permission> permissions, Set<RolePagePermission> rolePagePermisisons) {
        this.id = id;
        this.roleName = roleName;
        this.permissions = permissions;
        this.rolePagePermisisons = rolePagePermisisons;
    }


    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<Permission> getUserPermissions() {
        return permissions;
    }

    public void setUserPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
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
                ", permissions=" + permissions +
                ", rolePagePermisisons=" + rolePagePermisisons +
                '}';
    }
}
