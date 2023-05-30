package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTO;
import com.yondu.knowledgebase.DTO.directory.user_access.DirectoryUserAccessDTOMapper;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
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

    public DirectoryUserAccessDTO.BaseResponse addDirectoryUserAccess(Long directoryId, DirectoryUserAccessDTO.AddRequest request) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Directory not found with ID: "+ directoryId));

        User user = userRepository.findById(request.userId())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with ID: "+ request.userId()));

        Permission permission = permissionRepository.findById(request.permissionId())
                .orElseThrow(()-> new ResourceNotFoundException("Directory Permission not found with ID: "+ request.permissionId()));

        DirectoryUserAccess newDirectoryUserAccess = new DirectoryUserAccess(directory, user, permission);
        return DirectoryUserAccessDTOMapper.mapToBaseResponse(directoryUserAccessRepository.save(newDirectoryUserAccess));
    }

    public List<DirectoryUserAccessDTO.BaseResponse> getAllDirectoryUserAccess(Long directoryId) {
        List<DirectoryUserAccess> directoryUserAccesses = directoryUserAccessRepository.findAll();
        return directoryUserAccesses.stream().filter((data)->data.getDirectory().getId().equals(directoryId)).map(DirectoryUserAccessDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
    }

    public void removeDirectoryUserAccess(Long directoryId, Long id) {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Directory not found with ID: "+ directoryId));

        DirectoryUserAccess directoryUserAccess = directoryUserAccessRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Directory User Access not found"));

        directoryUserAccessRepository.delete(directoryUserAccess);
    }
}
