package com.yondu.knowledgebase.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<UserPermission> userPermissions = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<RolePagePermission> rolePagePermisisons = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<DirectoryRoleAccess> directoryRoleAccesses;

    public Role() {
    }

    public Role(Long id, String roleName, Set<UserPermission> userPermissions, Set<RolePagePermission> rolePagePermisisons, Set<DirectoryRoleAccess> directoryRoleAccesses) {
        this.id = id;
        this.roleName = roleName;
        this.userPermissions = userPermissions;
        this.rolePagePermisisons = rolePagePermisisons;
        this.directoryRoleAccesses = directoryRoleAccesses;
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

    public Set<DirectoryRoleAccess> getDirectoryRoleAccesses() {
        return directoryRoleAccesses;
    }

    public void setDirectoryRoleAccesses(Set<DirectoryRoleAccess> directoryRoleAccesses) {
        this.directoryRoleAccesses = directoryRoleAccesses;
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
                ", directoryRoleAccesses=" + directoryRoleAccesses +
                '}';
    }
}
