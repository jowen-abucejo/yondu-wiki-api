package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.role_access.DirectoryRoleAccessRequest;
import com.yondu.knowledgebase.DTO.directory.role_access.DirectoryRoleAccessResponse;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryPermission;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.entities.DirectoryRoleAccess;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.DirectoryPermissionRepository;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import com.yondu.knowledgebase.repositories.DirectoryRoleAccessRepository;
import com.yondu.knowledgebase.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryRoleAccessService {
    private final DirectoryRoleAccessRepository directoryRoleAccessRepository;
    private final DirectoryRepository directoryRepository;
    private final RoleRepository roleRepository;
    private final DirectoryPermissionRepository directoryPermissionRepository;

    public DirectoryRoleAccessService(DirectoryRoleAccessRepository directoryRoleAccessRepository, DirectoryRepository directoryRepository, RoleRepository roleRepository, DirectoryPermissionRepository directoryPermissionRepository) {
        this.directoryRoleAccessRepository = directoryRoleAccessRepository;
        this.directoryRepository = directoryRepository;
        this.roleRepository = roleRepository;
        this.directoryPermissionRepository = directoryPermissionRepository;
    }

    public DirectoryRoleAccessResponse addDirectoryRoleAccess(Long directoryId, DirectoryRoleAccessRequest request) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(()-> new NotFoundException("Directory not found with ID: "+ directoryId));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(()-> new NotFoundException("Role not found with ID: "+ request.getRoleId()));

        DirectoryPermission permission = directoryPermissionRepository.findByIdAndIsDeletedFalse(request.getPermissionId())
                .orElseThrow(()-> new NotFoundException("Directory permission not found with ID: "+ request.getPermissionId()));

        DirectoryRoleAccess newDirectoryRoleAccess = new DirectoryRoleAccess(directory, role, permission);
        return new DirectoryRoleAccessResponse(directoryRoleAccessRepository.save(newDirectoryRoleAccess));
    }

    public List<DirectoryRoleAccessResponse> getAllDirectoryRoleAccess(Long directoryId) {
        List<DirectoryRoleAccess> directoryRoleAccesses = directoryRoleAccessRepository.findAll();
        return directoryRoleAccesses.stream().filter((data)->data.getDirectory().getId().equals(directoryId)).map(DirectoryRoleAccessResponse::new)
                .collect(Collectors.toList());
    }
}
