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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowStepApproverRepository workflowStepApproverRepository;
    private final PermissionRepository permissionRepository;

    private Logger log = LoggerFactory.getLogger(DirectoryService.class);

    @Autowired
    private PageRepository pageRepository;
    private final DirectoryUserAccessRepository directoryUserAccessRepository;

    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRepository,
            PermissionRepository permissionRepository, WorkflowRepository workflowRepository,
            WorkflowStepRepository workflowStepRepository,
            WorkflowStepApproverRepository workflowStepApproverRepository,
            DirectoryUserAccessRepository directoryUserAccessRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.workflowRepository = workflowRepository;
        this.workflowStepRepository = workflowStepRepository;
        this.workflowStepApproverRepository = workflowStepApproverRepository;
        this.directoryUserAccessRepository = directoryUserAccessRepository;
    }

    public DirectoryDTO.GetResponse getDefaultDirectory() {
        Permission permission = permissionRepository.findById(19L)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Permission id '%d' not found", 19)));
        Directory root = directoryRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Directory id '%d' not found", 1)));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory defaultDirectory = traverseByLevel(root, permission, currentUser);

        if (defaultDirectory == null) {
            throw new AccessDeniedException();
        }

        Set<Directory> subdirectories = defaultDirectory.getSubDirectories().stream()
                .filter((dir) -> hasPermission(currentUser, dir, permission)).collect(Collectors.toSet());
        defaultDirectory.setSubDirectories(subdirectories);

        return DirectoryDTOMapper.mapToGetResponse(defaultDirectory);
    }

    public DirectoryDTO.GetResponse getDefaultDirectory(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Permission id '%d' not found", 19)));
        Directory root = directoryRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Directory id '%d' not found", 1)));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory defaultDirectory = traverseByLevel(root, permission, currentUser);

        if (defaultDirectory == null) {
            throw new AccessDeniedException();
        }

        Set<Directory> subdirectories = defaultDirectory.getSubDirectories().stream()
                .filter((dir) -> hasPermission(currentUser, dir, permission)).collect(Collectors.toSet());
        defaultDirectory.setSubDirectories(subdirectories);

        return DirectoryDTOMapper.mapToGetResponse(defaultDirectory);
    }

    public DirectoryDTO.GetResponse getDirectory(Long id) {
        Long permissionId = 19L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission 'id' not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", id)));

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        Set<Directory> subdirectories = directory.getSubDirectories().stream()
                .filter((dir) -> hasPermission(currentUser, dir, permission)).collect(Collectors.toSet());
        directory.setSubDirectories(subdirectories);

        return DirectoryDTOMapper.mapToGetResponse(directory);
    }

    public DirectoryDTO.GetResponse createDirectory(DirectoryDTO.CreateRequest request) {
        if (request.parentId() == null ||
                request.name() == null || request.description() == null ||
                request.name().isEmpty() || request.description().isEmpty() ||
                request.workflow() == null || request.workflow().isEmpty() ||
                request.workflow().stream()
                        .anyMatch(obj -> obj == null || obj.approvers() == null || obj.approvers().isEmpty())) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 16L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission ID not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory parent = directoryRepository.findById(request.parentId()).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", request.parentId())));

        if (!hasPermission(currentUser, parent, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new DuplicateResourceException(String.format("Directory name '%s' already exists", request.name()));
        }

        Directory savedDirectory = directoryRepository
                .save(new Directory(request.name(), request.description(), parent, currentUser, currentUser));
        Workflow savedWorkflow = workflowRepository.save(new Workflow(savedDirectory));
        request.workflow().forEach((step) -> {
            WorkflowStep savedWorkflowStep = workflowStepRepository
                    .save(new WorkflowStep(savedWorkflow, step.name(), step.step()));
            step.approvers().forEach(user -> workflowStepApproverRepository
                    .save(new WorkflowStepApprover(savedWorkflowStep, userRepository.findById(user.id()).orElseThrow(
                            () -> new ResourceNotFoundException(String.format("User id %d not found", user.id()))))));
        });
        savedDirectory.setWorkflow(savedWorkflow);
        savedDirectory = directoryRepository.save(savedDirectory);

        Directory finalSavedDirectory = savedDirectory;
        List<DirectoryUserAccess> newAccess = request.userAccess().stream()
                .map((access) -> new DirectoryUserAccess(finalSavedDirectory,
                        permissionRepository.findById(access.permissionId())
                                .orElseThrow(() -> new ResourceNotFoundException("Permission not found")),
                        userRepository.findById(access.userId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"))))
                .toList();
        directoryUserAccessRepository.saveAll(newAccess);

        return DirectoryDTOMapper.mapToGetResponse(savedDirectory);
    }

    // public List<DirectoryDTO.GetResponse>
    // moveDirectories(DirectoryDTO.MoveRequest request) {
    // if (request.ids() == null || request.parentId() == null ||
    // request.newParentId() == null) {
    // throw new RequestValidationException("Invalid request parameters");
    // }
    //
    // Long permissionId = 17L;
    // Permission permission =
    // permissionRepository.findById(permissionId).orElseThrow(()->new
    // ResourceNotFoundException(String.format("Directory permission id '%d' not
    // found", permissionId)));
    //
    // User currentUser =
    // (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //
    // List<DirectoryDTO.GetResponse> data = new ArrayList<>();
    // for (Long id: request.ids()) {
    // Directory directory = directoryRepository.findById(id).orElseThrow(() -> new
    // ResourceNotFoundException(String.format("Directory id '%d' not found", id)));
    // Directory newParentDirectory =
    // directoryRepository.findById(request.newParentId()).orElseThrow(()-> new
    // ResourceNotFoundException(String.format("New parent directory id '%d' not
    // found", request.newParentId())));
    //
    // if (!directory.getParent().getId().equals(request.parentId())) {
    // throw new ResourceNotFoundException(String.format("Directory '%s' with parent
    // id '%d' does not exists", directory.getName(), request.parentId()));
    // }
    //
    // if (!hasPermission(currentUser, directory, permission)) {
    // throw new AccessDeniedException();
    // }
    //
    // if (isDirectoryNameDuplicate(directory.getName(),
    // newParentDirectory.getSubDirectories())){
    // throw new DuplicateResourceException(String.format("Directory with same name
    // '%s' already exists in new parent's subdirectories", directory.getName()));
    // }
    //
    // directory.setParent(newParentDirectory);
    // Directory savedDirectory = directoryRepository.save(directory);
    // data.add(DirectoryDTOMapper.mapToGetResponse(savedDirectory));
    // }
    // return data;
    // }

    public DirectoryDTO.GetResponse moveDirectory(Long id, Long parentId, Long newParentId) {

        if (parentId == null || newParentId == null) {
            throw new RequestValidationException("Invalid request parameters");
        }

        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission id '%d' not found", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Directory id '%d' not found", id)));
        Directory newParentDirectory = directoryRepository.findById(newParentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("New parent directory id '%d' not found", newParentId)));

        if (!directory.getParent().getId().equals(parentId)) {
            throw new ResourceNotFoundException(
                    String.format("Directory '%s' with parent id '%d' does not exists", directory.getName(), parentId));
        }

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryNameDuplicate(directory.getName(), newParentDirectory.getSubDirectories())) {
            throw new DuplicateResourceException(
                    String.format("Directory with same name '%s' already exists in new parent's subdirectories",
                            directory.getName()));
        }

        directory.setParent(newParentDirectory);
        Directory savedDirectory = directoryRepository.save(directory);
        return DirectoryDTOMapper.mapToGetResponse(savedDirectory);
    }

    public DirectoryDTO.GetResponse renameDirectory(Long id, DirectoryDTO.RenameRequest request) {

        if (request.name() == null || request.name().isEmpty()) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission 'id' not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", id)));
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

    public DirectoryDTO.GetResponse editDirectory(Long id, DirectoryDTO.CreateRequest request) {

        if (request.parentId() == null ||
                request.name() == null || request.description() == null ||
                request.name().isEmpty() || request.description().isEmpty() ||
                request.workflow() == null || request.workflow().isEmpty() ||
                request.workflow().stream()
                        .anyMatch(obj -> obj == null || obj.approvers() == null || obj.approvers().isEmpty())) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission 'id' not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Directory 'id' not found: %d", id)));
        Directory parent = directory.getParent();

        if (parent == null) {
            throw new AccessDeniedException();
        }

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        directory.setName(request.name());
        directory.setDescription(request.description());
        directory.setDateModified(LocalDate.now());
        directory.setModifiedBy(currentUser);

        Workflow workflow = directory.getWorkflow();

        request.workflow().forEach(step -> {
            WorkflowStep workflowStep = workflowStepRepository.findByWorkflowAndStep(workflow, step.step())
                    .orElse(null);

            if (workflowStep == null) {
                WorkflowStep newWorkflowStep = workflowStepRepository
                        .save(new WorkflowStep(workflow, step.name(), step.step()));
                List<WorkflowStepApprover> workflowStepApprovers = step
                        .approvers().stream().map(
                                (approver) -> new WorkflowStepApprover(newWorkflowStep,
                                        userRepository.findById(approver.id())
                                                .orElseThrow(() -> new ResourceNotFoundException("User not found"))))
                        .toList();
                workflowStepApproverRepository.saveAll(workflowStepApprovers);
            } else {
                workflowStep.setName(step.name());

                workflowStepApproverRepository
                        .deleteAll(workflowStepApproverRepository.findAllByWorkflowStep(workflowStep));

                step.approvers().forEach(approver -> {
                    User user = userRepository.findById(approver.id())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    WorkflowStepApprover workflowStepApprover = workflowStepApproverRepository
                            .findByApproverAndWorkflowStep(user, workflowStep).orElse(null);
                    if (workflowStepApprover == null) {
                        workflowStepApproverRepository.save(new WorkflowStepApprover(workflowStep, user));
                    }
                });

                workflowStepRepository.save(workflowStep);
            }
        });

        List<DirectoryUserAccess> newAccesses = request.userAccess().stream().map(userAccess -> {
            Permission permission1 = permissionRepository.findById(userAccess.permissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
            User user1 = userRepository.findById(userAccess.user().id())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            DirectoryUserAccess directoryUserAccess = directoryUserAccessRepository
                    .findByDirectoryAndPermissionAndUser(directory, permission1, user1).orElse(null);
            if (directoryUserAccess == null) {
                return new DirectoryUserAccess(directory, permission1, user1);
            }
            return directoryUserAccess;
        }).toList();

        directoryUserAccessRepository.saveAll(newAccesses);

        List<DirectoryUserAccess> toRemoveAccesses = directory.getDirectoryUserAccesses()
                .stream()
                .filter(userAccess -> request.userAccess()
                        .stream()
                        .noneMatch(obj -> obj.permissionId().equals(userAccess.getPermission().getId())
                                && obj.user().id().equals(userAccess.getUser().getId())))
                .toList();

        directoryUserAccessRepository.deleteAll(toRemoveAccesses);

        Directory savedDirectory = directoryRepository.save(directory);
        return DirectoryDTOMapper.mapToGetResponse(savedDirectory);
    }

    public void removeDirectory(Long id) {
        Long permissionId = 18L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission ID not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Directory not found: " + id));

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

    private Directory traverseByLevel(Directory directory, Permission permission, User user) {
        if (hasPermission(user, directory, permission)) {
            return directory;
        }
        Iterator<Directory> subdirectories = directory.getSubDirectories().stream().iterator();
        while (subdirectories.hasNext()) {
            Directory child = traverseByLevel(subdirectories.next(), permission, user);
            if (child != null) {
                return child;
            }
        }
        // for (Directory child : directory.getSubDirectories()) {
        // traverseByLevel(child, permission, user);
        // }
        return null;
    }

    public boolean isDirectoryExists(String name, Directory parent) {
        Directory existingDirectory = directoryRepository.findByNameAndParent(name, parent).orElse(null);
        return existingDirectory != null;
    }

    private boolean hasPermission(User user, Directory directory, Permission permission) {
        if (user.getRole().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("administrator"))) {
            return true;
        }

        // directoryUserAccessRepo.findPermissionDirectoryUser() kung null walang
        // permission
        if (directory.getDirectoryUserAccesses().stream()
                .anyMatch((dua) -> dua.getUser().equals(user) && dua.getPermission().equals(permission))) {
            return true;
        }

        return false;
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

        org.springframework.data.domain.Page<Page> pages = pageRepository.getPagesFromDirectory(tempDir, type,
                pageRequest);
        if (pages.hasContent()) {
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
                                        .build())
                                .active(page.getActive())
                                .pageType(page.getType())
                                .build();

                        return pageDTO;
                    })
                    .collect(Collectors.toList());

            return new PaginatedResponse<PageDTO>(pageDTOs, 1, pageDTOs.size(), (long) pageDTOs.size());
        }

        return null;
    }

    /**
     * Retrieves a paginated list of directories to which the user has the specified
     * permission.
     * 
     * @param permission The specific permission to filter directories by.
     * @param userId     The ID of the user whose directories are being queried.
     * @param pageNumber The page number of the result set to retrieve.
     * @param pageSize   The number of directories to include in each page.
     * @return A paginated response containing the directories that match the
     *         specified criteria.
     */
    private PaginatedResponse<DirectoryDTO.GetResponse> getDirectoriesByUserAccess(
            com.yondu.knowledgebase.enums.Permission permission, Long userId, Integer pageNumber, Integer pageSize) {
        int retrievedPage = Math.max(1, pageNumber);
        Pageable pageRequest = PageRequest.of(retrievedPage - 1, pageSize);
        var optionalDirectories = directoryRepository
                .findByDirectoryUserAccessesPermissionNameAndDirectoryUserAccessesUserId(permission.getCode(), userId,
                        pageRequest)
                .orElse(null);

        var directoryDTOList = optionalDirectories.getContent().stream()
                .map(dir -> DirectoryDTOMapper.mapToGetResponse(dir)).collect(Collectors.toList());

        return new PaginatedResponse<>(directoryDTOList, retrievedPage, pageSize,
                optionalDirectories.getTotalElements());
    }

    /**
     * Retrieves a paginated list of directories to which the current user has the
     * "CREATE_CONTENT" permission.
     * 
     * @param page The page number of the result set to retrieve.
     * @param size The number of directories to include in each page.
     * @return A paginated response containing the directories that the current user
     *         can create content in.
     */
    public PaginatedResponse<DirectoryDTO.GetResponse> getDirectoriesWithCreateContentPermission(Integer page,
            Integer size) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getDirectoriesByUserAccess(com.yondu.knowledgebase.enums.Permission.CREATE_CONTENT, currentUser.getId(),
                page, size);
    }
}
