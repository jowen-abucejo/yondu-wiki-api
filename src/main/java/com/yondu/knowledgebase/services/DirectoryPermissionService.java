package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionRequest;
import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionResponse;
import com.yondu.knowledgebase.entities.DirectoryPermission;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.DirectoryPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryPermissionService {
    private final DirectoryPermissionRepository directoryPermissionRepository;

    public DirectoryPermissionService(DirectoryPermissionRepository directoryPermissionRepository) {
        this.directoryPermissionRepository = directoryPermissionRepository;
    }

    public DirectoryPermissionResponse createDirectoryPermission(DirectoryPermissionRequest request) {
        DirectoryPermission newPermission = new DirectoryPermission(request.getName(), request.getDescription());
        return new DirectoryPermissionResponse(directoryPermissionRepository.save(newPermission));
    }

    public List<DirectoryPermissionResponse> getAllDirectoryPermissions() {
        List<DirectoryPermission> permissions = directoryPermissionRepository.findByIsDeletedFalse();

        return permissions.stream()
                .map(DirectoryPermissionResponse::new)
                .collect(Collectors.toList());
    }

    public DirectoryPermissionResponse getDirectoryPermissionByID(Long id) {
        DirectoryPermission directoryPermission = directoryPermissionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()->new NotFoundException("Directory Permission not found with ID: " + id));

        return new DirectoryPermissionResponse(directoryPermission);
    }

    public DirectoryPermissionResponse updateDirectoryPermission(Long id, DirectoryPermissionRequest directoryPermissionRequest) {
        DirectoryPermission permission = directoryPermissionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new NotFoundException("Directory Permission not found with ID: " + id));

        permission.setName(directoryPermissionRequest.getName());
        permission.setDescription(directoryPermissionRequest.getDescription());

        return new DirectoryPermissionResponse(directoryPermissionRepository.save(permission));
    }

    public DirectoryPermissionResponse deleteDirectoryPermission(Long id) {
        DirectoryPermission permission = directoryPermissionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new NotFoundException("Directory Permission not found with ID: " + id));

        permission.setDeleted(true);
        return new DirectoryPermissionResponse(directoryPermissionRepository.save(permission));
    }
}
