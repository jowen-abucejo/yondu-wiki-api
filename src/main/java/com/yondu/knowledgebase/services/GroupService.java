package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.group.GroupDTOMapper;
import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.GroupRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<GroupDTO.BaseResponse> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .map(GroupDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
    }

    public GroupDTO.BaseResponse createGroup(GroupDTO.GroupRequest request) {
        if (request.name() == null || request.name().isEmpty() || request.description() == null || request.description().isEmpty()) {
            throw new RequestValidationException("Name and Description are required");
        }

        if (groupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(String.format("Group name '%s' already exists", request.name()));
        }

        Group savedGroup = groupRepository.save(new Group(request.name(), request.description()));
        for (Long id: request.members()) {
            savedGroup.getUsers().add(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User id '%d' not found", id))));
        }
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
