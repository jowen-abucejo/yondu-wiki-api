package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page_permission.user_access.UserPagePermissionDTO;

import java.util.List;
import java.util.Set;

public interface UserPagePermissionService {

    public Set<UserPagePermissionDTO.BaseResponse> addUserToPageAccess(Long pageId, UserPagePermissionDTO.AddUser userPermission);

    public UserPagePermissionDTO.BaseResponse removeUserToPageAccess(Long pageId, UserPagePermissionDTO.UserPermissionPair userPagePermission);

    public List<UserPagePermissionDTO.BaseResponse> getAllPagePermissionOfUser(Long id);

}

