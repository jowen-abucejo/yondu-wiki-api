package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.services.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllGroups() {
        List<GroupDTO.BaseResponse> data = groupService.getAllGroups();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Data retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createGroup(@RequestBody GroupDTO.GroupRequest request) {
        GroupDTO.BaseResponse data = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Group created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getGroupById(@PathVariable Long id) {
        GroupDTO.BaseResponse data = groupService.getGroupById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Data retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> editGroupById(@PathVariable Long id, @RequestBody GroupDTO.GroupRequest request) {
        GroupDTO.BaseResponse data = groupService.editGroupById(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Group updated successfully"));
    }

    @PostMapping("/{userGroupId}/add-user")
    public ResponseEntity<ApiResponse<?>> addUserToUserGroup(@PathVariable Long userGroupId, @RequestBody GroupDTO.EditUsersRequest request) {
        GroupDTO.BaseResponse data = groupService.addUserToUserGroup(userGroupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User add to group successfully"));
    }

    @DeleteMapping("/{userGroupId}/remove-user")
    public ResponseEntity<ApiResponse<?>> removeUserToUserGroup(@PathVariable Long userGroupId, @RequestBody GroupDTO.EditUsersRequest request) {
        GroupDTO.BaseResponse data = groupService.removeUserToUserGroup(userGroupId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "User removed from group successfully"));
    }

    @PostMapping("/{userGroupId}/pages/{pageId}/add-permission")
    public ResponseEntity<ApiResponse<?>> addUserGroupPermissionToPage(@PathVariable Long userGroupId, @PathVariable Long pageId, @RequestBody GroupDTO.AddPermission request) {
        GroupDTO.BaseResponse data = groupService.addUserGroupPermissionToPage(userGroupId, pageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Added Page Permission to User Group"));
    }

    @DeleteMapping("/{userGroupId}/pages/{pageId}/delete-permission")
    public ResponseEntity<ApiResponse<?>> deleteUserGroupPermissionToPage(@PathVariable Long userGroupId, @PathVariable Long pageId, @RequestBody GroupDTO.AddPermission request) {
        GroupDTO.BaseResponse data = groupService.removeUserGroupPermissionToPage(userGroupId, pageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Removed Page Permission to User Group"));
    }
}
