package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.user_group.UserGroupDTO;
import com.yondu.knowledgebase.services.UserGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user-group")
public class UserGroupController {


    private final UserGroupService userGroupService;
    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUserGroup(@RequestBody UserGroupDTO.UserGroupRequest request) {
        UserGroupDTO.GeneralResponse data = userGroupService.createUserGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User Group Created"));
    }

    @PostMapping("/{userGroupId}/add-user")
    public ResponseEntity<ApiResponse<?>> addUserToUserGroup(@PathVariable Long userGroupId, @RequestBody UserGroupDTO.EditUsers request) {
        UserGroupDTO.GeneralResponse data = userGroupService.addUserToUserGroup(userGroupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User Added to User Group"));
    }

    @DeleteMapping("/{userGroupId}/remove-user")
    public ResponseEntity<ApiResponse<?>> removeUserToUserGroup(@PathVariable Long userGroupId, @RequestBody UserGroupDTO.EditUsers request) {
        UserGroupDTO.GeneralResponse data = userGroupService.removeUserToUserGroup(userGroupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User Removed from User Group"));
    }

    @PostMapping("/{userGroupId}/pages/{pageId}/add-permission")
    public ResponseEntity<ApiResponse<?>> addUserGroupPermissionToPage(@PathVariable Long userGroupId, @PathVariable Long pageId, @RequestBody UserGroupDTO.AddPermisison request) {
        UserGroupDTO.GeneralResponse data = userGroupService.addUserGroupPermissionToPage(userGroupId, pageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Added Page Permission to User Group"));
    }

    @PostMapping("/{userGroupId}/pages/{pageId}/delete-permission")
    public ResponseEntity<ApiResponse<?>> deleteUserGroupPermissionToPage(@PathVariable Long userGroupId, @PathVariable Long pageId, @RequestBody UserGroupDTO.AddPermisison request) {
        UserGroupDTO.GeneralResponse data = userGroupService.removeUserGroupPermissionToPage(userGroupId, pageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Removed Page Permission to User Group"));
    }

}
