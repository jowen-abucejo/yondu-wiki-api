package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.role_access.DirectoryRoleAccessResponse;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryPermission;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.entities.DirectoryRoleAccess;
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

    public DirectoryRoleAccessResponse addDirectoryRoleAccess(Long directoryId, Long roleId, Long permissionId) {
        Directory directory = directoryRepository.findById(directoryId).orElseThrow(()-> new EntityNotFoundException("Directory not found with ID: "+directoryId));
        Role role = roleRepository.findById(roleId).orElseThrow(()-> new EntityNotFoundException("Role not found with ID: "+roleId));
        DirectoryPermission permission = directoryPermissionRepository.findByIdAndIsDeletedFalse(permissionId).orElseThrow(()-> new EntityNotFoundException("Directory permission not found with ID: "+permissionId));

        DirectoryRoleAccess newDirectoryRoleAccess = new DirectoryRoleAccess(directory, role, permission);
        DirectoryRoleAccess savedDirectoryRoleAccess = directoryRoleAccessRepository.save(newDirectoryRoleAccess);
        return new DirectoryRoleAccessResponse(savedDirectoryRoleAccess);
    }

    public List<DirectoryRoleAccessResponse> getAllDirectoryRoleAccess(Long directoryId) {
        List<DirectoryRoleAccess> directoryRoleAccesses = directoryRoleAccessRepository.findAll();
        return directoryRoleAccesses.stream().filter((data)->data.getDirectory().getId().equals(directoryId)).map(DirectoryRoleAccessResponse::new)
                .collect(Collectors.toList());
    }
}
