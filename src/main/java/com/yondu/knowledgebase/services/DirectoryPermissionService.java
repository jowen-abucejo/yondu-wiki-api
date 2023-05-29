package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionRequest;
import com.yondu.knowledgebase.DTO.directory.permission.DirectoryPermissionResponse;
import com.yondu.knowledgebase.entities.DirectoryPermission;
import com.yondu.knowledgebase.repositories.DirectoryPermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryPermissionService {
    private final DirectoryPermissionRepository directoryPermissionRepository;

    public DirectoryPermissionService(DirectoryPermissionRepository directoryPermissionRepository) {
        this.directoryPermissionRepository = directoryPermissionRepository;
    }

    public DirectoryPermissionResponse createDirectoryPermission(DirectoryPermissionRequest directoryPermissionRequest) {
        DirectoryPermission newPermission = new DirectoryPermission(directoryPermissionRequest.getName(), directoryPermissionRequest.getDescription());
        DirectoryPermission savedPermission = directoryPermissionRepository.save(newPermission);
        return new DirectoryPermissionResponse(savedPermission);
    }

    public List<DirectoryPermissionResponse> getAllDirectoryPermissions() {
        List<DirectoryPermission> permissions = directoryPermissionRepository.findByIsDeletedFalse();
        return permissions.stream()
                .map(DirectoryPermissionResponse::new)
                .collect(Collectors.toList());
    }

    public DirectoryPermissionResponse getDirectoryPermissionByID(Long id) {
        DirectoryPermission directoryPermission = directoryPermissionRepository.findByIdAndIsDeletedFalse(id).orElseThrow(()->new EntityNotFoundException("Permission not found with ID:" + id));
        return new DirectoryPermissionResponse(directoryPermission);
    }

    public DirectoryPermissionResponse updateDirectoryPermission(Long id, DirectoryPermissionRequest directoryPermissionRequest) {
        //validate data
        DirectoryPermission permission = directoryPermissionRepository.findByIdAndIsDeletedFalse(id).orElseThrow(()-> new EntityNotFoundException("Permission not found with ID:"+id));
        permission.setName(directoryPermissionRequest.getName());
        permission.setDescription(directoryPermissionRequest.getDescription());
        DirectoryPermission updatedPermission = directoryPermissionRepository.save(permission);
        return new DirectoryPermissionResponse(updatedPermission);
    }

    public DirectoryPermissionResponse deleteDirectoryPermission(Long id) {
        DirectoryPermission permission = directoryPermissionRepository.findByIdAndIsDeletedFalse(id).orElseThrow(()-> new EntityNotFoundException("Permission not found with ID:"+id));
        permission.setDeleted(true);
        DirectoryPermission updatedPermission = directoryPermissionRepository.save(permission);
        return new DirectoryPermissionResponse(updatedPermission);
    }
}
