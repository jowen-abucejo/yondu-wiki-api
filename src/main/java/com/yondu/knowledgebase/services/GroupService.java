package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.group.GroupDTOMapper;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.*;
import com.yondu.knowledgebase.repositories.GroupRepository;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final Logger log = LoggerFactory.getLogger(GroupService.class);

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, PermissionRepository permissionRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<GroupDTO.BaseResponse> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .map(GroupDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
    }

    public PaginatedResponse<GroupDTO.ShortResponse> getAllGroups(Long permissionId, String searchKey, int page, int size) {
        log.info("GroupService.getAllGroups()");
        log.info("permissionId : " + permissionId);
        log.info("searchKey : " + searchKey);
        log.info("page : " + page);
        log.info("size : " + size);

        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException("No permission found."));
        searchKey = "%" + searchKey;

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Group> groups = groupRepository.findAllByNameAndPermission(searchKey, permission, pageRequest);

        if(groups.hasContent()){
            List<GroupDTO.ShortResponse> groupDTOs = groups.stream()
                            .map(GroupDTOMapper::mapToShortResponse)
                            .collect(Collectors.toList());

            PaginatedResponse<GroupDTO.ShortResponse> paginatedResponse = new PaginatedResponse<>(groupDTOs, page, size, (long) groups.getTotalPages());
            return paginatedResponse;
        }else{
            throw new NoContentException("No Contents found");
        }
    }

    public PaginatedResponse<GroupDTO.BaseResponse> getAllGroupsPaginated(String searchKey, String statusFilter, int page, int size) {
        log.info("GroupService.getAllGroupsPaginated()");
        log.info("searchKey: " + searchKey);
        log.info("statusFilter: " + statusFilter);
        log.info("page: " + page);
        log.info("size: " + size);

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Group> groupPage;

        if (!statusFilter.isEmpty() && !searchKey.isEmpty()) {
            groupPage = groupRepository.findAllByNameAndStatus(searchKey, Boolean.parseBoolean(statusFilter), pageRequest);
        } else if (!statusFilter.isEmpty()) {
            groupPage = groupRepository.findAllByStatus(Boolean.parseBoolean(statusFilter), pageRequest);
        } else if(!searchKey.isEmpty()){
            groupPage = groupRepository.findAllByName(searchKey, pageRequest);
        } else {
            groupPage = groupRepository.findAll(pageRequest);
        }

        List<Group> groups = groupPage.getContent();

        if (groups.isEmpty()) {
            throw new NoContentException("No content found");
        }

        List<GroupDTO.BaseResponse> userDTOs = groups.stream()
                .map(GroupDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());

        PaginatedResponse<GroupDTO.BaseResponse> paginatedResponse = new PaginatedResponse<>(userDTOs, page, size, (long) groupPage.getTotalPages());

        return paginatedResponse;
    }


    public GroupDTO.BaseResponse createGroup(GroupDTO.GroupRequest request) {
        if (request.name() == null || request.name().isEmpty() || request.description() == null || request.description().isEmpty()) {
            throw new RequestValidationException("Name and Description are required");
        }

        if (groupRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException(String.format("Group name '%s' already exists", request.name()));
        }

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Group savedGroup = groupRepository.save(new Group(request.name(), request.description(), currentUser));
        for (Long id: request.members()) {
            savedGroup.getUsers().add(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User id '%d' not found", id))));
        }

        Set<Permission> groupPermissions = request.permissions().stream().map(id -> permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission not found"))).collect(Collectors.toSet());
        savedGroup.getPermissions().addAll(groupPermissions);
        savedGroup = groupRepository.save(savedGroup);

        return GroupDTOMapper.mapToBaseResponse(savedGroup);
    }

    public GroupDTO.BaseResponse getGroupById(Long id) {
        Group group = groupRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + id));
        return GroupDTOMapper.mapToBaseResponse(group);
    }

    public GroupDTO.BaseResponse editGroupById(Long id, GroupDTO.UpdateGroupRequest request) {
        if (id == 1) {
            throw new AccessDeniedException();
        }

        if (request.name() == null || request.name().isEmpty() || request.description() == null || request.description().isEmpty()) {
            throw new RequestValidationException("name must not be empty");
        }

        Group group = groupRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + id));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Group groupToCompare = groupRepository.findByNameIgnoreCase(request.name()).orElse(null);
        // check if name already exists
        if (groupToCompare != null && !groupToCompare.equals(group) ) {
            throw new DuplicateResourceException(String.format("Group name '%s' already exists!", request.name()));
        }

        group.setName(request.name());
        group.setDescription(request.description());
        group.setModifiedBy(currentUser);
        group.setDateModified(LocalDateTime.now());
        group.setActive(request.isActive());

        Set<User> members = new HashSet<>();
        for (Long memberId : request.members()) {
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
            members.add(member);
        }
        group.setUsers(members);

        Set<Permission> permissions = new HashSet<>();
        for (Long permissionId : request.permissions()) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
            permissions.add(permission);
        }
        group.setPermissions(permissions);

        Group savedGroup = groupRepository.save(group);
        return GroupDTOMapper.mapToBaseResponse(savedGroup);
    }


    public GroupDTO.BaseResponse addUserToGroup(Long id, GroupDTO.UserRequest request) {
        if (request.email() == null || request.email().isEmpty()) {
            throw new RequestValidationException("Email is required");
        }

        Group group = groupRepository.findByIdAndIsActive(id, true).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + id));
        User user = userRepository.findByEmail(request.email()).orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + request.email()));

        if (group.getUsers().contains(user)) {
            throw new DuplicateResourceException("User already exists in group");
        }

        group.getUsers().add(user);
        groupRepository.save(group);
        return GroupDTOMapper.mapToBaseResponse(group);
    }

    public GroupDTO.BaseResponse removeUserFromGroup(Long id, GroupDTO.UserRequest request) {
        if (request.email() == null || request.email().isEmpty()) {
            throw new RequestValidationException("Email is required");
        }

        Group group = groupRepository.findByIdAndIsActive(id, true).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + id));
        User user = userRepository.findByEmail(request.email()).orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + request.email()));

        if (!group.getUsers().contains(user)) {
            throw new RequestValidationException("User does not exist in group");
        }

        group.getUsers().remove(user);
        groupRepository.save(group);
        return GroupDTOMapper.mapToBaseResponse(group);
    }

    public GroupDTO.BaseResponse inactivateGroup(GroupDTO.AddRightsRequest groupId) {
        Group group = groupRepository.findById(groupId.groupId()).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + groupId));
        group.setActive(false);
        groupRepository.save(group);
        return GroupDTOMapper.mapToBaseResponse(group);
    }

    public GroupDTO.BaseResponse activateGroup(GroupDTO.AddRightsRequest groupId) {
        Group group = groupRepository.findById(groupId.groupId()).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + groupId));
        group.setActive(true);
        groupRepository.save(group);
        return GroupDTOMapper.mapToBaseResponse(group);
    }

    public GroupDTO.BaseResponse addUserGroupPermissionToPage(Long userGroupId, Long pageId, GroupDTO.AddPermission request) {
        return null;
    }
    public GroupDTO.BaseResponse removeUserGroupPermissionToPage(Long userGroupId, Long pageId, GroupDTO.AddPermission request) {
        return null;
    }

    public List<GroupDTO.GroupPermissions> getMyGroups() {
        log.info("GroupService.getMyGroups()");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Group> groups = groupRepository.findAllGroupsByUser(user);
        if(groups.isEmpty()){
            throw new NoContentException("You have no groups.");
        }

        List<GroupDTO.GroupPermissions> groupDTOs = groups.stream().map(GroupDTOMapper::mapToGroupPermission).collect(Collectors.toList());
        return groupDTOs;
    }

}
