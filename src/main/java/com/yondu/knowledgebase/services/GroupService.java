package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.group.GroupDTOMapper;
import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
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

        if (isGroupExists(request.name())) {
            throw new DuplicateResourceException(String.format("Group name '%s' already exists", request.name()));
        }

        Group savedGroup = groupRepository.save(new Group(request.name(), request.description()));
        return GroupDTOMapper.mapToBaseResponse(savedGroup);
    }

    public GroupDTO.BaseResponse getGroupById(Long id) {
        Group group = groupRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + id));
        return GroupDTOMapper.mapToBaseResponse(group);
    }

    public GroupDTO.BaseResponse editGroupById(Long id, GroupDTO.GroupRequest request) {
        Group group = groupRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Group not found with ID: " + id));

        if (request.name() != null && !request.name().isEmpty()) {
            if (isGroupExists(request.name())) {
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


    public GroupDTO.BaseResponse addUserToUserGroup(Long userGroupId, GroupDTO.EditUsersRequest request) {
        return null;
    }

    public GroupDTO.BaseResponse removeUserToUserGroup(Long userGroupId, GroupDTO.EditUsersRequest request) {
        return null;
    }

    public GroupDTO.BaseResponse addUserGroupPermissionToPage(Long userGroupId, Long pageId, GroupDTO.AddPermission request) {
        return null;
    }
    public GroupDTO.BaseResponse removeUserGroupPermissionToPage(Long userGroupId, Long pageId, GroupDTO.AddPermission request) {
        return null;
    }

    public boolean isGroupExists(String name) {
        return groupRepository.existsByName(name);
    }
}
