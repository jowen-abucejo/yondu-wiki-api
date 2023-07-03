package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.services.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/groups")
public class GroupController {

    private final Logger log = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllGroups() {
        List<GroupDTO.BaseResponse> data = groupService.getAllGroups();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Data retrieved successfully"));
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupDTO.BaseResponse>>> getAllGroupsPaginated(
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(defaultValue = "") String statusFilter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        log.info("GroupController.getAllUsersPaginated()");
        log.info("searchKey: " + searchKey);
        log.info("statusFilter: " + statusFilter);
        log.info("page: " + page);
        log.info("size: " + size);

        ResponseEntity<ApiResponse<PaginatedResponse<GroupDTO.BaseResponse>>> response;

        PaginatedResponse<GroupDTO.BaseResponse> fetchedUsers = groupService.getAllGroupsPaginated(searchKey, statusFilter, page, size);

        ApiResponse<PaginatedResponse<GroupDTO.BaseResponse>> apiResponse = ApiResponse.success(fetchedUsers, "success");
        response = ResponseEntity.ok(apiResponse);

        return response;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createGroup(@RequestBody GroupDTO.GroupRequest request) {
        GroupDTO.BaseResponse data = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Group created successfully"));
    }

    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse<?>> inactivateGroup(@RequestBody GroupDTO.AddRightsRequest groupId) {
        GroupDTO.BaseResponse data = groupService.inactivateGroup(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Group deleted successfully"));
    }
    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<?>> activateGroup(@RequestBody GroupDTO.AddRightsRequest groupId) {
        GroupDTO.BaseResponse data = groupService.activateGroup(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Group deleted successfully"));
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

    @PostMapping("/{id}/add-user")
    public ResponseEntity<ApiResponse<?>> addUserToGroup(@PathVariable Long id, @RequestBody GroupDTO.UserRequest request) {
        GroupDTO.BaseResponse data = groupService.addUserToGroup(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "User added to group successfully"));
    }

    @DeleteMapping("/{id}/remove-user")
    public ResponseEntity<ApiResponse<?>> removeUserToGroup(@PathVariable Long id, @RequestBody GroupDTO.UserRequest request) {
        GroupDTO.BaseResponse data = groupService.removeUserFromGroup(id, request);
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

    /**
     * Get all active groups based on
     * permission given.
     *
     * @param permissionId
     * @param searchKey
     * @param page
     * @param size
     *
     */
    @GetMapping("/permissions/{permission_id}")
    public ResponseEntity<ApiResponse<PaginatedResponse<GroupDTO.ShortResponse>>> getGroupsBasedOnPermission(@PathVariable(name="permission_id") Long permissionId,
                                                                                               @RequestParam(defaultValue = "") String searchKey,
                                                                                               @RequestParam(defaultValue = "1") int page,
                                                                                               @RequestParam(defaultValue = "5") int size) {
        PaginatedResponse<GroupDTO.ShortResponse> data = groupService.getAllGroups(permissionId, searchKey, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Data retrieved successfully"));
    }
}
