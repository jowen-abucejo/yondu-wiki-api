package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.DirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
import com.yondu.knowledgebase.DTO.directory.request.CreateDirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.response.CreateDirectoryResponse;
import com.yondu.knowledgebase.DTO.directory.response.RenameDirectoryResponse;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.DirectoryPermissionRepository;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final DirectoryPermissionRepository directoryPermissionRepository;

    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRepository, DirectoryPermissionRepository directoryPermissionRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.directoryPermissionRepository = directoryPermissionRepository;
    }

    public CreateDirectoryResponse createDirectory(Long parentId, CreateDirectoryRequest request) {
        // get permission
        String requiredPermissionName = "Create Directory";
        DirectoryPermission permission = directoryPermissionRepository.findByNameAndIsDeletedFalse(requiredPermissionName).orElseThrow(() -> new NotFoundException("'Create Directory' permission not found"));

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));

        // get parent directory
        Directory parent = directoryRepository.findById(parentId).orElseThrow(() -> new NotFoundException("Parent directory not found: " + parentId));

        if (!parent.userHasAccess(currentUser, permission)) {
            //throw new AccessDeniedException();
            System.out.println("Access denied");
        }

        // save directory
        Directory savedDirectory = directoryRepository.save(new Directory(request.getName(), request.getDescription(), parent));
        return new CreateDirectoryResponse(savedDirectory);
    }

    public RenameDirectoryResponse renameDirectory(Long id, String name) {
        // get permission
        String requiredPermissionName = "Edit Directory";
        DirectoryPermission permission = directoryPermissionRepository.findByNameAndIsDeletedFalse(requiredPermissionName).orElseThrow(() -> new NotFoundException("'Edit Directory' permission not found"));

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));

        // get directory
        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Directory not found: " + id));

        if (!directory.userHasAccess(currentUser, permission)) {
            //throw new AccessDeniedException();
            System.out.println("Access denied");
        }

        directory.setName(name);
        directory.setDateModified(LocalDate.now());
        Directory savedDirectory = directoryRepository.save(directory);
        return new RenameDirectoryResponse(savedDirectory);
    }

    public void removeDirectory(Long id) {
        // get permission
        String requiredPermissionName = "Delete Directory";
        DirectoryPermission permission = directoryPermissionRepository.findByNameAndIsDeletedFalse(requiredPermissionName).orElseThrow(() -> new NotFoundException("'Delete Directory' permission not found"));

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));

        // get directory
        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Directory not found: " + id));

        if (!directory.userHasAccess(currentUser, permission)) {
            //throw new AccessDeniedException();
            System.out.println("Access denied");
        }

        directoryRepository.delete(directory);
    }

}
