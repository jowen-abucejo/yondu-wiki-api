package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.*;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import com.yondu.knowledgebase.repositories.DirectoryRightsRepository;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final DirectoryRightsRepository directoryRightsRepository;

    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRepository, PermissionRepository permissionRepository, DirectoryRightsRepository directoryRightsRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.directoryRightsRepository = directoryRightsRepository;
    }

    public DirectoryDTO.GetResponse getDirectory(Long id) {
        Long permissionId = 19L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission 'id' not found: %d", permissionId)));

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", id)));

        if (!hasDirectoryUserRights(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        return DirectoryDTOMapper.mapToGetResponse(directory);
    }

    public DirectoryDTO.BaseResponse createDirectory(Long parentId, DirectoryDTO.CreateRequest request) {

        if (request.name() == null || request.description() == null ||
                request.name().isEmpty() || request.description().isEmpty()) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 16L;
        Permission permission = permissionRepository.findById( permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission ID not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory parent = directoryRepository.findById(parentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", parentId)));

        if (!hasDirectoryUserRights(currentUser, parent, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new DuplicateResourceException(String.format("Directory name '%s' already exists", request.name()));
        }

        Directory savedDirectory = directoryRepository.save(new Directory(request.name(), request.description(), parent, currentUser));
        List<DirectoryRights> savedRights = directoryRightsRepository
                .saveAll(permissionRepository
                        .findAllByCategory("Directory")
                        .stream()
                        .map(obj -> directoryRightsRepository.save(new DirectoryRights(savedDirectory, obj))).toList());

        Set<Rights> updatedRights = new HashSet<>(currentUser.getRights());
        updatedRights.addAll(savedRights);

        currentUser.setRights(updatedRights);
        userRepository.save(currentUser);

        return DirectoryDTOMapper.mapToBaseResponse(savedDirectory);
    }

    public DirectoryDTO.BaseResponse renameDirectory(Long id, DirectoryDTO.RenameRequest request) {

        if (request.name() == null ||request.name().isEmpty()) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission 'id' not found: %d", permissionId)));

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", id)));
        Directory parent = directory.getParent();

        if (!hasDirectoryUserRights(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        if (parent == null) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new DuplicateResourceException(String.format("Directory name '%s' already exists", request.name()));
        }

        directory.setName(request.name());
        directory.setDateModified(LocalDate.now());
        Directory savedDirectory = directoryRepository.save(directory);
        return DirectoryDTOMapper.mapToBaseResponse(savedDirectory);
    }

    public void removeDirectory(Long id) {
        Long permissionId = 18L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission ID not found: %d", permissionId)));

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Directory not found: " + id));

        if (!hasDirectoryUserRights(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }


        System.out.println("directory.getParent() == null");
        if (directory.getParent() == null) {
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

    public boolean hasDirectoryUserRights(User user, Directory directory, Permission desiredPermission) {
        return user.getRights().stream().anyMatch(rights ->
            ((DirectoryRights) rights).getDirectory().equals(directory) &&
                    ((DirectoryRights) rights).getPermission().equals(desiredPermission)
        );
    }


}
