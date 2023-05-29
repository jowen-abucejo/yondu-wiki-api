package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionService {

    private final PermissionRepository permissionRepository;

    public UserPermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<Permission> getAllPermission(){
        return permissionRepository.findAll();
    }

    public Permission addPermission(Permission permission) {

        permissionRepository.save(permission);
        return permission;
    }
}
