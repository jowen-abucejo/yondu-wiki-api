package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.RolePagePermission;

import java.util.List;

public interface RolePagePermissionService {

    public RolePagePermission addUserRoleToPageAccess(RolePagePermission pagePermission);

    public RolePagePermission removeUserRoleToPageAccess(Long id);

    public List<RolePagePermission> getAllPagePermissionOfUserRole(Long id);

    public List<RolePagePermission> getAllUserRolesOfPagePermission(Long id);

}

