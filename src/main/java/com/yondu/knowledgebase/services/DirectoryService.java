package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.directory.*;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page.UserDTO;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.*;
import com.yondu.knowledgebase.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    private Logger log = LoggerFactory.getLogger(DirectoryService.class);

    @Autowired
    private PageRepository pageRepository;

    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRepository, PermissionRepository permissionRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public DirectoryDTO.GetResponse getDefaultDirectory() {
        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!hasPermission(currentUser, null, null)) {
            throw new AccessDeniedException();
        }

        Directory directory = directoryRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", 1)));
        return DirectoryDTOMapper.mapToGetResponse(directory);
    }

    public DirectoryDTO.GetResponse getDirectory(Long id) {
        Long permissionId = 19L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission 'id' not found: %d", permissionId)));

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", id)));

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        return DirectoryDTOMapper.mapToGetResponse(directory);
    }

    public DirectoryDTO.GetResponse createDirectory(Long parentId, DirectoryDTO.CreateRequest request) {

        if (request.name() == null || request.description() == null ||
                request.name().isEmpty() || request.description().isEmpty()) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 16L;
        Permission permission = permissionRepository.findById( permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission ID not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory parent = directoryRepository.findById(parentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", parentId)));

        if (!hasPermission(currentUser, parent, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new DuplicateResourceException(String.format("Directory name '%s' already exists", request.name()));
        }

        Directory savedDirectory = directoryRepository.save(new Directory(request.name(), request.description(), parent, currentUser));

        return DirectoryDTOMapper.mapToGetResponse(savedDirectory);
    }

//    public List<DirectoryDTO.GetResponse> moveDirectories(DirectoryDTO.MoveRequest request) {
//        if (request.ids() == null || request.parentId() == null || request.newParentId() == null) {
//            throw new RequestValidationException("Invalid request parameters");
//        }
//
//        Long permissionId = 17L;
//        Permission permission = permissionRepository.findById(permissionId).orElseThrow(()->new ResourceNotFoundException(String.format("Directory permission id '%d' not found", permissionId)));
//
//        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        List<DirectoryDTO.GetResponse> data = new ArrayList<>();
//        for (Long id: request.ids()) {
//            Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory id '%d' not found", id)));
//            Directory newParentDirectory = directoryRepository.findById(request.newParentId()).orElseThrow(()-> new ResourceNotFoundException(String.format("New parent directory id '%d' not found", request.newParentId())));
//
//            if (!directory.getParent().getId().equals(request.parentId())) {
//                throw new ResourceNotFoundException(String.format("Directory '%s' with parent id '%d' does not exists", directory.getName(), request.parentId()));
//            }
//
//            if (!hasPermission(currentUser, directory, permission)) {
//                throw new AccessDeniedException();
//            }
//
//            if (isDirectoryNameDuplicate(directory.getName(), newParentDirectory.getSubDirectories())){
//                throw new DuplicateResourceException(String.format("Directory with same name '%s' already exists in new parent's subdirectories", directory.getName()));
//            }
//
//            directory.setParent(newParentDirectory);
//            Directory savedDirectory = directoryRepository.save(directory);
//            data.add(DirectoryDTOMapper.mapToGetResponse(savedDirectory));
//        }
//        return data;
//    }

    public DirectoryDTO.GetResponse moveDirectory(Long id, Long parentId, Long newParentId) {

        if (parentId == null || newParentId == null) {
             throw new RequestValidationException("Invalid request parameters");
        }

        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(()->new ResourceNotFoundException(String.format("Directory permission id '%d' not found", permissionId)));

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory id '%d' not found", id)));
        Directory newParentDirectory = directoryRepository.findById(newParentId).orElseThrow(()-> new ResourceNotFoundException(String.format("New parent directory id '%d' not found", newParentId)));

        if (!directory.getParent().getId().equals(parentId)) {
            throw new ResourceNotFoundException(String.format("Directory '%s' with parent id '%d' does not exists", directory.getName(), parentId));
        }

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryNameDuplicate(directory.getName(), newParentDirectory.getSubDirectories())){
            throw new DuplicateResourceException(String.format("Directory with same name '%s' already exists in new parent's subdirectories", directory.getName()));
        }

        directory.setParent(newParentDirectory);
        Directory savedDirectory = directoryRepository.save(directory);
        return DirectoryDTOMapper.mapToGetResponse(savedDirectory);
    }

    public DirectoryDTO.GetResponse renameDirectory(Long id, DirectoryDTO.RenameRequest request) {

        if (request.name() == null ||request.name().isEmpty()) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission 'id' not found: %d", permissionId)));

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", id)));
        Directory parent = directory.getParent();

        if (parent == null) {
            throw new AccessDeniedException();
        }

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new DuplicateResourceException(String.format("Directory name '%s' already exists", request.name()));
        }

        directory.setName(request.name());
        directory.setDateModified(LocalDate.now());
        Directory savedDirectory = directoryRepository.save(directory);
        return DirectoryDTOMapper.mapToGetResponse(savedDirectory);
    }

    public void removeDirectory(Long id) {
        Long permissionId = 18L;
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(String.format("Directory permission ID not found: %d", permissionId)));

        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Directory not found: " + id));

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }


        if (isNotEmptyDirectory(directory)) {
            throw new ResourceDeletionException("Directory is not empty");
        }

        directoryRepository.delete(directory);
    }

    public boolean isNotEmptyDirectory(Directory directory) {
        if (!directory.getPages().isEmpty() || !directory.getSubDirectories().isEmpty()) {
            return true;
        }

        return false;
    }

    public boolean isDirectoryExists(String name, Directory parent) {
        Directory existingDirectory = directoryRepository.findByNameAndParent(name, parent).orElse(null);
        return existingDirectory != null;
    }

    private boolean hasPermission(User user, Directory directory, Permission permission) {
        return user.getRole().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("administrator"));
    }

    private boolean isDirectoryNameDuplicate(String directoryName, Set<Directory> subdirectories) {
        for (Directory subdirectory : subdirectories) {
            if (subdirectory.getName().equals(directoryName)) {
                return true;
            }
        }
        return false;
    }


    public PaginatedResponse<PageDTO> getPages(Long directoryId, int pageNumber, int size, String type) {
        log.info("DirectoryService.getPages()");
        log.info("directoryId : " + directoryId);
        log.info("pageNumber : " + pageNumber);
        log.info("size : " + size);
        log.info("type : " + type);

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size);

        Directory tempDir = new Directory();
        tempDir.setId(directoryId);

        org.springframework.data.domain.Page<Page> pages = pageRepository.getPagesFromDirectory(tempDir, type, pageRequest);
        if(pages.hasContent()){
            List<PageDTO> pageDTOs = pages
                    .stream()
                    .map(page -> {
                        PageDTO pageDTO = new PageDTO.PageDTOBuilder()
                                .id(page.getId())
                                .dateCreated(page.getDateCreated())
                                .author(new UserDTO.UserDTOBuilder()
                                        .id(page.getAuthor().getId())
                                        .email(page.getAuthor().getEmail())
                                        .firstName(page.getAuthor().getFirstName())
                                        .lastName(page.getAuthor().getLastName())
                                        .position(page.getAuthor().getPosition())
                                        .build()
                                )
                                .active(page.getActive())
                                .pageType(page.getType())
                                .build();

                        return pageDTO;
                    })
                    .collect(Collectors.toList());

            return new PaginatedResponse<PageDTO>(pageDTOs, 1, pageDTOs.size(), (long)pageDTOs.size());
        }

        return null;
    }
}
