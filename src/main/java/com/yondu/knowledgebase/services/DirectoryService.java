package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.*;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.AlreadyExistException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRepository, PermissionRepository permissionRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public DirectoryDTO.GetResponse getDirectory(Long id) {
        Long permissionId = 19L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException(String.format("Directory permission 'id' not found: %d", permissionId)));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format("User 'email' not found: %s", email)));

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Directory 'id' not found: %d", id)));

        if (hasDirectoryUserAccess(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        return DirectoryDTOMapper.mapToGetResponse(directory);
    }

    public DirectoryDTO.BaseResponse createDirectory(Long parentId, DirectoryDTO.CreateRequest request) {
        Long permissionId = 16L;
        Permission permission = permissionRepository.findById( permissionId).orElseThrow(() -> new NotFoundException(String.format("Directory permission ID not found: %d", permissionId)));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format("User 'email' not found: %s", email)));

        Directory parent = directoryRepository.findById(parentId).orElseThrow(() -> new NotFoundException(String.format("Directory 'id' not found: %d", parentId)));

        if (hasDirectoryUserAccess(currentUser, parent, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new AlreadyExistException(String.format("Directory name '%s' already exists", request.name()));
        }

        Directory savedDirectory = directoryRepository.save(new Directory(request.name(), request.description(), parent, currentUser));
        return DirectoryDTOMapper.mapToBaseResponse(savedDirectory);
    }

    public DirectoryDTO.BaseResponse renameDirectory(Long id, DirectoryDTO.RenameRequest request) {
        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException(String.format("Directory permission 'id' not found: %d", permissionId)));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format("User 'email' not found: %s", email)));

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Directory 'id' not found: %d", id)));
        Directory parent = directory.getParent();

        if (hasDirectoryUserAccess(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        if (parent == null) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new AlreadyExistException(String.format("Directory name '%s' already exists", request.name()));
        }

        directory.setName(request.name());
        directory.setDateModified(LocalDate.now());
        Directory savedDirectory = directoryRepository.save(directory);
        return DirectoryDTOMapper.mapToBaseResponse(savedDirectory);
    }

    public void removeDirectory(Long id) {
        Long permissionId = 18L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException(String.format("Directory permission ID not found: %d", permissionId)));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Directory not found: " + id));

        if (hasDirectoryUserAccess(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        if (isNotEmptyDirectory(directory)) {
            // throw bawal i delete
        }

        directoryRepository.delete(directory);
    }

    public boolean isNotEmptyDirectory(Directory directory) {
        if (!directory.getPages().isEmpty()) {
            return true;
        }

        for (Directory subdirectory : directory.getSubDirectories()) {
            if (isNotEmptyDirectory(subdirectory)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDirectoryExists(String name, Directory parent) {
        Directory existingDirectory = directoryRepository.findByNameAndParent(name, parent).orElse(null);
        return existingDirectory != null;
    }

    public boolean hasDirectoryUserAccess(User user, Directory directory, Permission desiredPermission) {
        Set<Permission> userPermissions = getUserPermissions(user, directory);
        return userPermissions.contains(desiredPermission);
    }

    private Set<Permission> getUserPermissions(User user, Directory directory) {
        return getDirectoryUserAccess(directory)
                .stream()
                .map(access -> access.getUser().equals(user) ? access.getPermission(): null)
                .collect(Collectors.toSet());
    }

    public Set<DirectoryUserAccess> getDirectoryUserAccess(Directory directory) {
        if(directory == null) {
            return new HashSet<>();
        }
        return directory.getDirectoryUserAccesses() == null || directory.getDirectoryUserAccesses().isEmpty() ? getDirectoryUserAccess(directory.getParent()):directory.getDirectoryUserAccesses();
    }

}
