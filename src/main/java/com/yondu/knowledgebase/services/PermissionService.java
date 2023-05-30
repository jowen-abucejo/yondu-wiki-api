package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<PermissionDTO.BaseResponse> getAllPermission(){
        return permissionRepository.findAll().stream().map(PermissionDTOMapper::mapToBaseResponse).toList();
    }

    public PermissionDTO.BaseResponse getPermission(Long id) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission with id " + id + " not found"));
        return PermissionDTOMapper.mapToBaseResponse(permission);
    }
}
