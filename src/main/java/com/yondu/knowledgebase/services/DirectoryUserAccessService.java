package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessRequest;
import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessResponse;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.*;
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

    public DirectoryUserAccessResponse addDirectoryUserAccess(Long directoryId, DirectoryUserAccessRequest request) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(()-> new NotFoundException("Directory not found with ID: "+ directoryId));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()-> new NotFoundException("User not found with ID: "+ request.getUserId()));

        Permission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(()-> new NotFoundException("Directory Permission not found with ID: "+ request.getPermissionId()));

        DirectoryUserAccess newDirectoryUserAccess = new DirectoryUserAccess(directory, user, permission);
        return new DirectoryUserAccessResponse(directoryUserAccessRepository.save(newDirectoryUserAccess));
    }

    public List<DirectoryUserAccessResponse> getAllDirectoryUserAccess(Long directoryId) {
        List<DirectoryUserAccess> directoryUserAccesses = directoryUserAccessRepository.findAll();
        return directoryUserAccesses.stream().filter((data)->data.getDirectory().getId().equals(directoryId)).map(DirectoryUserAccessResponse::new)
                .collect(Collectors.toList());
    }

    public void removeDirectoryUserAccess(Long directoryId, Long directoryUserAccessId) {
        DirectoryUserAccess directoryUserAccess = directoryUserAccessRepository.findByDirectoryIdAndId(directoryId, directoryUserAccessId)
                .orElseThrow(()-> new NotFoundException("Directory User Access not found"));

        directoryUserAccessRepository.delete(directoryUserAccess);
    }
}
