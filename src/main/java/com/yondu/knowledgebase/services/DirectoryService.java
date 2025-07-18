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
    private final GroupRepository groupRepository;
    private final DirectoryGroupAccessRepository directoryGroupAccessRepository;

    private Logger log = LoggerFactory.getLogger(DirectoryService.class);

    @Autowired
    private PageRepository pageRepository;
    private final DirectoryUserAccessRepository directoryUserAccessRepository;

    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRepository,
            PermissionRepository permissionRepository, WorkflowRepository workflowRepository,
            WorkflowStepRepository workflowStepRepository,
            WorkflowStepApproverRepository workflowStepApproverRepository,
            DirectoryUserAccessRepository directoryUserAccessRepository,
            GroupRepository groupRepository,
            DirectoryGroupAccessRepository directoryGroupAccessRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.workflowRepository = workflowRepository;
        this.workflowStepRepository = workflowStepRepository;
        this.workflowStepApproverRepository = workflowStepApproverRepository;
        this.directoryUserAccessRepository = directoryUserAccessRepository;
        this.groupRepository = groupRepository;
        this.directoryGroupAccessRepository = directoryGroupAccessRepository;
    }

    public DirectoryDTO.GetResponse getDefaultDirectory() {
        Permission permission = permissionRepository.findById(19L)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Permission id '%d' not found", 19)));
        Directory root = directoryRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory id '%d' not found", 1)));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory defaultDirectory = traverseByLevel(root, permission, currentUser);

        if (defaultDirectory == null) {
            throw new AccessDeniedException();
        }

        Set<Directory> subdirectories = defaultDirectory.getSubDirectories().stream()
                .filter((dir) -> hasPermission(currentUser, dir, permission))
                .collect(Collectors.toSet());
        defaultDirectory.setSubDirectories(subdirectories);

        return DirectoryDTOMapper.mapToGetResponse(defaultDirectory);
    }

    public DirectoryDTO.GetResponse getDefaultDirectory(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Permission id '%d' not found", 19)));
        Directory root = directoryRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory id '%d' not found", 1)));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory defaultDirectory = traverseByLevel(root, permission, currentUser);

        if (defaultDirectory == null) {
            throw new AccessDeniedException();
        }

        Set<Directory> subdirectories = defaultDirectory.getSubDirectories().stream()
                .filter((dir) -> hasPermission(currentUser, dir, permission))
                .collect(Collectors.toSet());
        defaultDirectory.setSubDirectories(subdirectories);

        return DirectoryDTOMapper.mapToGetResponse(defaultDirectory);
    }

    public DirectoryDTO.GetResponse getDirectory(Long id) {
        Long permissionId = 19L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission 'id' not found: %d",
                                permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory 'id' not found: %d", id)));

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        Set<Directory> subdirectories = directory.getSubDirectories().stream()
                .filter((dir) -> hasPermission(currentUser, dir, permission))
                .collect(Collectors.toSet());
        directory.setSubDirectories(subdirectories);

        return DirectoryDTOMapper.mapToGetResponse(directory);
    }

    public DirectoryDTO.GetResponse createDirectory(DirectoryDTO.CreateRequest request) {
        if (request.parentId() == null ||
                request.name() == null || request.description() == null ||
                request.name().isEmpty() || request.description().isEmpty() ||
                request.workflow() == null || request.workflow().isEmpty() ||
                request.workflow().stream()
                        .anyMatch(obj -> obj == null || obj.approvers() == null
                                || obj.approvers().isEmpty())) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 16L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission ID not found: %d", permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory parent = directoryRepository.findById(request.parentId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format("Directory 'id' not found: %d", request.parentId())));

        if (!hasPermission(currentUser, parent, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new DuplicateResourceException(
                    String.format("Directory name '%s' already exists", request.name()));
        }

        Directory savedDirectory = directoryRepository
                .save(new Directory(request.name(), request.description(), parent, currentUser,
                        currentUser));
        Workflow savedWorkflow = workflowRepository.save(new Workflow(savedDirectory));
        request.workflow().forEach((step) -> {
            WorkflowStep savedWorkflowStep = workflowStepRepository
                    .save(new WorkflowStep(savedWorkflow, step.name(), step.step()));
            step.approvers().forEach(user -> workflowStepApproverRepository
                    .save(new WorkflowStepApprover(savedWorkflowStep,
                            userRepository.findById(user.id()).orElseThrow(
                                    () -> new ResourceNotFoundException(String
                                            .format("User id %d not found",
                                                    user.id()))))));
        });
        savedDirectory.setWorkflow(savedWorkflow);
        directoryRepository.save(savedDirectory);

        /**
         * Save the creator's
         * access to the user directory access.
         */
        List<DirectoryUserAccess> creatorAccesses = creatorUserAccess(savedDirectory, currentUser);
        directoryUserAccessRepository.saveAll(creatorAccesses);

        List<DirectoryUserAccess> newUserAccess = request.userAccess().stream()
                .map((access) -> new DirectoryUserAccess(savedDirectory,
                        permissionRepository.findById(access.permissionId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Permission not found")),
                        userRepository.findById(access.user().id())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "User not found"))))
                .toList();
        directoryUserAccessRepository.saveAll(newUserAccess);

        List<DirectoryGroupAccess> newGroupAccess = request.groupAccess().stream()
                .map((access) -> new DirectoryGroupAccess(savedDirectory,
                        permissionRepository.findById(access.permissionId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Permission not found")),
                        groupRepository.findById(access.group().id())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Group not found"))))
                .toList();
        directoryGroupAccessRepository.saveAll(newGroupAccess);

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
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory id '%d' not found", id)));
        Directory newParentDirectory = directoryRepository.findById(newParentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("New parent directory id '%d' not found", newParentId)));

        if (!directory.getParent().getId().equals(parentId)) {
            throw new ResourceNotFoundException(
                    String.format("Directory '%s' with parent id '%d' does not exists",
                            directory.getName(), parentId));
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
                        String.format("Directory permission 'id' not found: %d",
                                permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory 'id' not found: %d", id)));
        Directory parent = directory.getParent();

        if (parent == null) {
            throw new AccessDeniedException();
        }

        if (!hasPermission(currentUser, directory, permission)) {
            throw new AccessDeniedException();
        }

        if (isDirectoryExists(request.name(), parent)) {
            throw new DuplicateResourceException(
                    String.format("Directory name '%s' already exists", request.name()));
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
                        .anyMatch(obj -> obj == null || obj.approvers() == null
                                || obj.approvers().isEmpty())) {
            throw new RequestValidationException("Invalid request body");
        }

        Long permissionId = 17L;
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory permission 'id' not found: %d",
                                permissionId)));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Directory 'id' not found: %d", id)));
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

        List<WorkflowStep> toRemoveSteps = workflow.getSteps()
                .stream()
                .filter(existingStep -> request.workflow()
                        .stream().filter(step -> step.id() != null)
                        .noneMatch(step -> step.id().equals(existingStep.getId())))
                .toList();

        toRemoveSteps.forEach(removeSteps -> workflow.getSteps().remove(removeSteps));
        workflowStepRepository.deleteAll(toRemoveSteps);

        request.workflow().forEach(step -> {
            WorkflowStep workflowStep = workflowStepRepository.findByWorkflowAndId(workflow, step.id())
                    .orElse(null);

            if (workflowStep == null) {
                WorkflowStep newWorkflowStep = workflowStepRepository
                        .save(new WorkflowStep(workflow, step.name(), step.step()));
                List<WorkflowStepApprover> workflowStepApprovers = step
                        .approvers().stream().map(
                                (approver) -> new WorkflowStepApprover(newWorkflowStep,
                                        userRepository.findById(approver.id())
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                        "User not found"))))
                        .toList();
                workflowStepApproverRepository.saveAll(workflowStepApprovers);
            } else {
                workflowStep.setName(step.name());
                workflowStep.setStep(step.step());
                workflowStepApproverRepository
                        .deleteAll(workflowStepApproverRepository
                                .findAllByWorkflowStep(workflowStep));

                step.approvers().forEach(approver -> {
                    User user = userRepository.findById(approver.id())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "User not found"));
                    WorkflowStepApprover workflowStepApprover = workflowStepApproverRepository
                            .findByApproverAndWorkflowStep(user, workflowStep).orElse(null);
                    if (workflowStepApprover == null) {
                        workflowStepApproverRepository
                                .save(new WorkflowStepApprover(workflowStep, user));
                    }
                });

                workflowStepRepository.save(workflowStep);
            }
        });

        List<DirectoryUserAccess> newUserAccesses = request.userAccess().stream().map(userAccess -> {
            Permission permission1 = permissionRepository.findById(userAccess.permissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
            User user = userRepository.findById(userAccess.user().id())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            DirectoryUserAccess directoryUserAccess = directoryUserAccessRepository
                    .findByDirectoryAndPermissionAndUser(directory, permission1, user).orElse(null);
            if (directoryUserAccess == null) {
                return new DirectoryUserAccess(directory, permission1, user);
            }
            return directoryUserAccess;
        }).toList();
        directoryUserAccessRepository.saveAll(newUserAccesses);

        List<DirectoryGroupAccess> newGroupAccesses = request.groupAccess().stream().map(groupAccess -> {
            Permission permission1 = permissionRepository.findById(groupAccess.permissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
            Group group = groupRepository.findById(groupAccess.group().id())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

            DirectoryGroupAccess directoryGroupAccess = directoryGroupAccessRepository
                    .findByDirectoryAndPermissionAndGroup(directory, permission1, group)
                    .orElse(null);
            if (directoryGroupAccess == null) {
                return new DirectoryGroupAccess(directory, permission1, group);
            }
            return directoryGroupAccess;
        }).toList();
        directoryGroupAccessRepository.saveAll(newGroupAccesses);

        List<DirectoryUserAccess> toRemoveUserAccesses = directory.getDirectoryUserAccesses()
                .stream()
                .filter(userAccess -> request.userAccess()
                        .stream()
                        .noneMatch(obj -> obj.permissionId()
                                .equals(userAccess.getPermission().getId())
                                && obj.user().id()
                                        .equals(userAccess.getUser().getId())))
                .toList();
        toRemoveUserAccesses.forEach(directory.getDirectoryUserAccesses()::remove);
        directoryUserAccessRepository.deleteAll(toRemoveUserAccesses);

        List<DirectoryGroupAccess> toRemoveGroupAccesses = directory.getDirectoryGroupAccesses()
                .stream()
                .filter(groupAccess -> request.groupAccess()
                        .stream()
                        .noneMatch(obj -> obj.permissionId()
                                .equals(groupAccess.getPermission().getId())
                                && obj.group().id().equals(
                                        groupAccess.getGroup().getId())))
                .toList();
        toRemoveGroupAccesses.forEach(directory.getDirectoryGroupAccesses()::remove);
        directoryGroupAccessRepository.deleteAll(toRemoveGroupAccesses);

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
        return directory.getPages().stream()
                .anyMatch(page -> !page.getDeleted() || !directory.getSubDirectories().isEmpty());
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

        if (directory.getDirectoryUserAccesses().stream()
                .anyMatch((dua) -> dua.getUser().equals(user)
                        && dua.getPermission().equals(permission))) {
            return true;
        }

        if (user.getGroups().stream()
                .anyMatch((group) -> directoryGroupAccessRepository
                        .findByDirectoryAndPermissionAndGroup(directory, permission, group)
                        .orElse(null) != null)) {
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
                                        .firstName(page.getAuthor()
                                                .getFirstName())
                                        .lastName(page.getAuthor()
                                                .getLastName())
                                        .position(page.getAuthor()
                                                .getPosition())
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
    private PaginatedResponse<DirectoryDTO.GetMinimizeResponse> getDirectoriesByUserAccess(
            com.yondu.knowledgebase.enums.Permission permission, Long userId, Integer pageNumber, Integer pageSize) {
        int retrievedPage = Math.max(1, pageNumber);
        Pageable pageRequest = PageRequest.of(retrievedPage - 1, pageSize);
        var optionalDirectories = directoryRepository
                .findByDirectoryUserAccessesPermissionNameAndDirectoryUserAccessesUserId(
                        permission.getCode(), userId, pageRequest)
                .orElse(null);

        var directoryDTOList = optionalDirectories.getContent().stream()
                .map(dir -> DirectoryDTOMapper.mapToGetMinimizeResponse(dir)).collect(Collectors.toList());

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
    public PaginatedResponse<DirectoryDTO.GetMinimizeResponse> getDirectoriesWithCreateContentPermission(
            Integer page, Integer size) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getDirectoriesByUserAccess(com.yondu.knowledgebase.enums.Permission.CREATE_CONTENT,
                currentUser.getId(), page, size);
    }

    public List<DirectoryUserAccess> creatorUserAccess(Directory directory, User user) {
        if (user == null) {
            throw new UserException("User Not Found");
        }

        List<Permission> permissionsToBeAddedToCreator = new ArrayList<>();
        permissionsToBeAddedToCreator
                .add(new Permission(com.yondu.knowledgebase.enums.Permission.VIEW_DIRECTORY.getId()));
        permissionsToBeAddedToCreator
                .add(new Permission(com.yondu.knowledgebase.enums.Permission.CREATE_CONTENT.getId()));
        permissionsToBeAddedToCreator
                .add(new Permission(com.yondu.knowledgebase.enums.Permission.UPDATE_CONTENT.getId()));
        permissionsToBeAddedToCreator
                .add(new Permission(com.yondu.knowledgebase.enums.Permission.READ_CONTENT.getId()));

        return permissionsToBeAddedToCreator.stream()
                .map(permission -> new DirectoryUserAccess(directory, permission, user))
                .collect(Collectors.toList());
    }
}
