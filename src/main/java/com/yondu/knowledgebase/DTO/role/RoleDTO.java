package com.yondu.knowledgebase.DTO.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.RolePagePermission;

import java.util.Set;

public class RoleDTO {

    private Long id;
    private String roleName;
    private Set<Permission> permission;
    @JsonIgnore

    private Set<RolePagePermission> rolePagePermissions;

    public RoleDTO(Long id, String roleName, Set<Permission> permission, Set<RolePagePermission> rolePagePermissions) {
        this.id = id;
        this.roleName = roleName;
        this.permission = permission;
        this.rolePagePermissions = rolePagePermissions;
    }

    public RoleDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Permission> getPermission() {
        return permission;
    }

    public void setPermission(Set<Permission> permission) {
        this.permission = permission;
    }

    public Set<RolePagePermission> getRolePagePermissions() {
        return rolePagePermissions;
    }

    public void setRolePagePermissions(Set<RolePagePermission> rolePagePermissions) {
        this.rolePagePermissions = rolePagePermissions;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", permission=" + permission +
                ", rolePagePermissions=" + rolePagePermissions +
                '}';
    }


}
