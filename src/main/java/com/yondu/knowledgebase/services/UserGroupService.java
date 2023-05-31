package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.user_group.UserGroupDTO;
import org.springframework.stereotype.Service;

@Service
public class UserGroupService {
    public UserGroupDTO.GeneralResponse createUserGroup(UserGroupDTO.UserGroupRequest request) {
        return null;
    }

    public UserGroupDTO.GeneralResponse addUserToUserGroup(Long userGroupId, UserGroupDTO.EditUsers request) {
        return null;
    }

    public UserGroupDTO.GeneralResponse removeUserToUserGroup(Long userGroupId, UserGroupDTO.EditUsers request) {
        return null;
    }

    public UserGroupDTO.GeneralResponse addUserGroupPermissionToPage(Long userGroupId, Long pageId, UserGroupDTO.AddPermisison request) {
        return null;
    }
    public UserGroupDTO.GeneralResponse removeUserGroupPermissionToPage(Long userGroupId, Long pageId, UserGroupDTO.AddPermisison request) {
        return null;
    }
}
