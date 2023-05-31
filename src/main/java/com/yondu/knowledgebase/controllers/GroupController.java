package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.services.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user-group")
public class GroupController {

    private final GroupService groupService;
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUserGroup(@RequestBody GroupDTO.UserGroupRequest request) {
        GroupDTO.GeneralResponse data = groupService.createUserGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User Group Created"));
    }

    @PostMapping("/{userGroupId}/add-user")
    public ResponseEntity<ApiResponse<?>> addUserToUserGroup(@PathVariable Long userGroupId, @RequestBody GroupDTO.EditUsersRequest request) {
        GroupDTO.GeneralResponse data = groupService.addUserToUserGroup(userGroupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User add to group successfully"));
    }

    @DeleteMapping("/{userGroupId}/remove-user")
    public ResponseEntity<ApiResponse<?>> removeUserToUserGroup(@PathVariable Long userGroupId, @RequestBody GroupDTO.EditUsersRequest request) {
        GroupDTO.GeneralResponse data = groupService.removeUserToUserGroup(userGroupId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "User removed from group successfully"));
    }

    @PostMapping("/{userGroupId}/pages/{pageId}/add-permission")
    public ResponseEntity<ApiResponse<?>> addUserGroupPermissionToPage(@PathVariable Long userGroupId, @PathVariable Long pageId, @RequestBody GroupDTO.AddPermission request) {
        GroupDTO.GeneralResponse data = groupService.addUserGroupPermissionToPage(userGroupId, pageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Added Page Permission to User Group"));
    }

    @DeleteMapping("/{userGroupId}/pages/{pageId}/delete-permission")
    public ResponseEntity<ApiResponse<?>> deleteUserGroupPermissionToPage(@PathVariable Long userGroupId, @PathVariable Long pageId, @RequestBody GroupDTO.AddPermission request) {
        GroupDTO.GeneralResponse data = groupService.removeUserGroupPermissionToPage(userGroupId, pageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Removed Page Permission to User Group"));
    }

    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<?>> getAllGroups() {
        GroupDTO.BaseResponse data = groupService.getAllGroups();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Data retrieved successfully"));
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<ApiResponse<?>> editGroupById(@PathVariable Long id, @RequestBody GroupDTO.EditGroupRequest request) {
        GroupDTO.BaseResponse data = groupService.editGroupById(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Group updated successfully"));
    }


}
