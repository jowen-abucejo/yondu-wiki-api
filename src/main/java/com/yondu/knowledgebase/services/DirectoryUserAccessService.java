package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTO;
import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTOMapper;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryUserAccessService {
    private final DirectoryUserAccessRepository directoryUserAccessRepository;
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public DirectoryUserAccessService(DirectoryUserAccessRepository directoryUserAccessRepository, DirectoryRepository directoryRepository, UserRepository userRepository, PermissionRepository permissionRepository) {
        this.directoryUserAccessRepository = directoryUserAccessRepository;
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public DirectoryUserAccessDTO.BaseResponse addDirectoryUserRights(Long directoryId, DirectoryUserAccessDTO.AddRequest request) {
        if (hasManageDirectoryPermission()) {
            throw new AccessDeniedException();
        }

        if (request.email() == null || request.email().isEmpty() || request.permissionId() == null) {
            throw new RequestValidationException("Email and Permission ID are required");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with email: "+ request.email()));

        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Directory not found with ID: "+ directoryId));

        Permission permissions = permissionRepository.findById(request.permissionId())
                .orElseThrow(()-> new ResourceNotFoundException("Directory Permission not found with ID: "+ request.permissionId()));

        if (directoryUserAccessRepository.existsByUserAndPermissionAndDirectory(user, permissions, directory)) {
            throw new DuplicateResourceException("Directory User Access already exists");
        }

        DirectoryUserAccess savedDirectoryUserAccess = directoryUserAccessRepository.save(new DirectoryUserAccess(directory, user, permissions));
        return DirectoryUserAccessDTOMapper.mapToBaseResponse(savedDirectoryUserAccess);
    }

    public List<DirectoryUserAccessDTO.BaseResponse> getAllDirectoryUserRights(Long directoryId) {
        if (hasManageDirectoryPermission()) {
            throw new AccessDeniedException();
        }

        List<DirectoryUserAccess> directoryUserAccesses = directoryUserAccessRepository.findByDirectoryId(directoryId);

        return directoryUserAccesses.stream()
                .map(DirectoryUserAccessDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
    }

    public void removeDirectoryUserRights(Long directoryId, Long directoryUserAccessId) {
        if (hasManageDirectoryPermission()) {
            throw new AccessDeniedException();
        }

        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Directory not found with ID: "+ directoryId));

        DirectoryUserAccess directoryUserAccess = directoryUserAccessRepository.findById(directoryUserAccessId)
                .orElseThrow(()-> new ResourceNotFoundException("Directory User Access not found"));

        directoryUserAccessRepository.delete(directoryUserAccess);
    }

    public boolean hasManageDirectoryPermission(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + email));

        Long requiredPermission = 25L;
        Permission permission = permissionRepository.findById(requiredPermission).orElseThrow(()-> new ResourceNotFoundException("Directory Permission not found with ID: "+ requiredPermission));

        return currentUser.getDirectoryUserAccesses()
                .stream()
                .noneMatch(access -> access.getPermission().getId().equals(permission.getId()));
    }
}
