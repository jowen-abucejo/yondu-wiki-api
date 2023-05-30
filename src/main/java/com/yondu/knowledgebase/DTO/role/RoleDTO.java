package com.yondu.knowledgebase.DTO.role;

import com.yondu.knowledgebase.entities.Permission;

import java.util.Set;

public class RoleDTO {

    private Long id;
    private String roleName;
    private Set<Permission> permission;

    public RoleDTO(Long id, String roleName, Set<Permission> permission) {
        this.id = id;
        this.roleName = roleName;
        this.permission = permission;
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

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", permission=" + permission +
                '}';
    }


}
