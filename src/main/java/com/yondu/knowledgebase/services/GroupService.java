package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    public GroupDTO.GeneralResponse createUserGroup(GroupDTO.UserGroupRequest request) {
        return null;
    }

    public GroupDTO.GeneralResponse addUserToUserGroup(Long userGroupId, GroupDTO.EditUsersRequest request) {
        return null;
    }

    public GroupDTO.GeneralResponse removeUserToUserGroup(Long userGroupId, GroupDTO.EditUsersRequest request) {
        return null;
    }

    public GroupDTO.GeneralResponse addUserGroupPermissionToPage(Long userGroupId, Long pageId, GroupDTO.AddPermission request) {
        return null;
    }
    public GroupDTO.GeneralResponse removeUserGroupPermissionToPage(Long userGroupId, Long pageId, GroupDTO.AddPermission request) {
        return null;
    }

    public GroupDTO.BaseResponse getAllGroups() {
        return null;
    }

    public GroupDTO.BaseResponse editGroupById(Long id, GroupDTO.EditGroupRequest request) {
        return null;
    }
}
