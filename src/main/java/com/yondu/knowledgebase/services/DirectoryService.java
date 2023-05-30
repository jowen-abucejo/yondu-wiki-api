package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.*;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.NotFoundException;
import com.yondu.knowledgebase.repositories.DirectoryRepository;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public DirectoryDTO.GetResponse getDirectory(Long parentId) {
        // get permission
        //palitan nalang yung value
//        Long permissionId = 1L;
//        Permission permission = permissionRepository.findById( permissionId).orElseThrow(() -> new NotFoundException("'Read Directory' permission not found"));
//
//        // get current user
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));

        // get parent directory
        Directory directory = directoryRepository.findById(parentId).orElseThrow(() -> new NotFoundException("Parent directory not found: " + parentId));

//        if (!directory.userHasAccess(currentUser, permission)) {
//            System.out.println("Access denied");
//            throw new AccessDeniedException();
//        }

        return DirectoryDTOMapper.mapToGetResponse(directory);
    }

    public DirectoryDTO.BaseResponse createDirectory(Long parentId, DirectoryDTO.CreateRequest request) {
        // get permission
        //palitan nalang yung value
        Long permissionId = 1L;
        Permission permission = permissionRepository.findById( permissionId).orElseThrow(() -> new NotFoundException("'Create Directory' permission not found"));

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));

        // get parent directory
        Directory parent = directoryRepository.findById(parentId).orElseThrow(() -> new NotFoundException("Parent directory not found: " + parentId));

        if (!parent.userHasAccess(currentUser, permission)) {
            System.out.println("Access denied");
            throw new AccessDeniedException();
        }

        // save directory
        Directory savedDirectory = directoryRepository.save(new Directory(request.name(), request.description(), parent));
        return DirectoryDTOMapper.mapToBaseResponse(savedDirectory);
    }

    public DirectoryDTO.BaseResponse renameDirectory(Long id, DirectoryDTO.RenameRequest request) {
        // get permission
        //palitan nalang yung value
        Long permissionId = 1L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("'Edit Directory' permission not found"));

        // get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));

        // get directory
        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Directory not found: " + id));

        if (!directory.userHasAccess(currentUser, permission)) {
            //throw new AccessDeniedException();
            System.out.println("Access denied");
        }

        directory.setName(request.name());
        directory.setDateModified(LocalDate.now());
        Directory savedDirectory = directoryRepository.save(directory);
        return DirectoryDTOMapper.mapToBaseResponse(savedDirectory);
    }

    public void removeDirectory(Long id) {
        // get permission
        //palitan nalang yung value
        Long permissionId = 1L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("'Edit Directory' permission not found"));

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
