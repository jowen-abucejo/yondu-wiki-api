package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.UserPermission;
import com.yondu.knowledgebase.repositories.UserPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionService(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    public List<UserPermission> getAllPermission(){
        return userPermissionRepository.findAll();
    }
}
