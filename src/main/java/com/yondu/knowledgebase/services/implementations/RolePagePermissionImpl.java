package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.entities.RolePagePermission;
import com.yondu.knowledgebase.repositories.RolePagePermissionRepository;
import com.yondu.knowledgebase.services.RolePagePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePagePermissionImpl implements RolePagePermissionService {

    private final RolePagePermissionRepository rolePagePermissionRepository;

    public RolePagePermissionImpl(RolePagePermissionRepository rolePagePermissionRepository){
        this.rolePagePermissionRepository = rolePagePermissionRepository;
    }

    @Override
    public RolePagePermission addUserRoleToPageAccess(RolePagePermission pagePermission) {
        return null;
    }

    @Override
    public RolePagePermission removeUserRoleToPageAccess(Long id) {
        return null;
    }

    @Override
    public List<RolePagePermission> getAllPagePermissionOfUserRole(Long id) {
        return null;
    }

    @Override
    public List<RolePagePermission> getAllUserRolesOfPagePermission(Long id) {
        return null;
    }
}
