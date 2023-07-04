package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.group.GroupDTOMapper;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.NoContentException;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.GroupRepository;
import com.yondu.knowledgebase.repositories.PermissionRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

        if (groupRepository.existsByName(request.name())) {
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

    public GroupDTO.BaseResponse editGroupById(Long id, GroupDTO.GroupRequest request) {
        Group group = groupRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + id));

        if (request.name() != null && !request.name().isEmpty()) {
            if (groupRepository.existsByName(request.name())) {
                throw new DuplicateResourceException(String.format("Group name '%s' already exists", request.name()));
            }
            group.setName(request.name());
        }

        if (request.description() != null && !request.description().isEmpty()){
            group.setDescription(request.description());
        }

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

}
