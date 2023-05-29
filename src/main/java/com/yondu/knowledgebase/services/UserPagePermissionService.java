package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page_permission.user_access.UserPagePermissionDTO;

import java.util.List;

public interface UserPagePermissionService {

    public UserPagePermissionDTO.BaseResponse addUserToPageAccess(Long permissionId, UserPagePermissionDTO.AddUser pagePermission);

    public UserPagePermissionDTO.BaseResponse removeUserToPageAccess(Long permissionId, UserPagePermissionDTO.AddUser pagePermission);

    public List<UserPagePermissionDTO.BaseResponse> getAllPagePermissionOfUser(Long id);

}

