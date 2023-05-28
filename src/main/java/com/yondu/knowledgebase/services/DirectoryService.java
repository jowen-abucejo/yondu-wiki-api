package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.DirectoryRequest;
import com.yondu.knowledgebase.DTO.directory.DirectoryResponse;
import com.yondu.knowledgebase.entities.*;
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

    public DirectoryResponse createDirectory(Long parentId, DirectoryRequest request) {
        // get permission
        String requiredPermissionName = "Create Directory";
        DirectoryPermission permission = directoryPermissionRepository.findByNameAndIsDeletedFalse(requiredPermissionName).orElseThrow(NullPointerException::new);

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getUserByEmail(email); // need exception

        // get parent directory
        Directory parent = directoryRepository.findById(parentId).orElseThrow(EntityNotFoundException::new);

        if (parent.userHasAccess(currentUser, permission)) {
            // throw access denied
        }

        // save directory
        Directory savedDirectory = directoryRepository.save(new Directory(request.getName(), request.getDescription(), parent));
        return new DirectoryResponse(savedDirectory);
    }

    public DirectoryResponse renameDirectory(Long id, String name) {
        // get permission
        String requiredPermissionName = "Edit Directory";
        DirectoryPermission permission = directoryPermissionRepository.findByNameAndIsDeletedFalse(requiredPermissionName).orElseThrow(NullPointerException::new);

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getUserByEmail(email); // need exception

        // get directory
        Directory directory = directoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (directory.userHasAccess(currentUser, permission)) {
            // throw access denied
        }

        directory.setName(name);
        directory.setDateModified(LocalDate.now());
        Directory savedDirectory = directoryRepository.save(directory);
        return new DirectoryResponse(savedDirectory);
    }

    public String removeDirectory(Long id) {
        // get permission
        String requiredPermissionName = "Edit Directory";
        DirectoryPermission permission = directoryPermissionRepository.findByNameAndIsDeletedFalse(requiredPermissionName).orElseThrow(NullPointerException::new);

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.getUserByEmail(email); // need exception

        Directory directory = directoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (directory.userHasAccess(currentUser, permission)) {
            // throw access denied
        }

        directoryRepository.delete(directory);
        return "Directory deleted successfully";
    }

    public static boolean findRolePermission_RoleAccessDirectory(Set<RoleDirectoryAccess> roleDirectoryAccesses, String requiredPermission, String requiredRole) {
        return roleDirectoryAccesses.stream().anyMatch((rda) -> rda.getRole().getRoleName().equals(requiredRole)
                                                            && rda.getPermission().getName().equals(requiredPermission));
    }
}
