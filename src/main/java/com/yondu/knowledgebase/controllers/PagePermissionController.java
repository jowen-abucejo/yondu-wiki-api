package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page_permission.user_access.UserPagePermissionDTO;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.UserPagePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
public class PagePermissionController {

    private final UserPagePermissionService userPagePermissionService;

    public PagePermissionController(UserPagePermissionService userPagePermissionService){
        this.userPagePermissionService = userPagePermissionService;
    }

    /**
     * Adding user roles in page permission roles
     * **/

    @PostMapping("/page-permissions/pages/{pageId}/addPageAccess")
    @PreAuthorize("hasAuthority('MANAGE_PAGE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<?>> addUserToPageAccess(@PathVariable Long pageId, @RequestBody UserPagePermissionDTO.AddUser userPermission){

        if(userPermission.userPermissionPair().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Please provide users and permissions"));
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userPagePermissionService.addUserToPageAccess(pageId, userPermission), "User has been successfully added in page permission."));
        } catch(DuplicateResourceException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        } catch (ResourceNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Cannot add user to page permission."));
        }
    }

    @PostMapping("/page-permissions/pages/{pageId}/removePageAccess")
    @PreAuthorize("hasAuthority('MANAGE_PAGE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<?>> removeUserToPageAccess(@PathVariable Long pageId, @RequestBody UserPagePermissionDTO.UserPermissionPair userPagePermission){
        if(userPagePermission.permissionId() == null || userPagePermission.userId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Incomplete Fields"));
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userPagePermissionService.removeUserToPageAccess(pageId, userPagePermission), "User has been successfully removed in page permission."));
        } catch (ResourceNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Cannot add user to page permission."));
        }

    }

    @GetMapping("/page-permissions/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getAllPageOfUser(@PathVariable Long userId){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userPagePermissionService.getAllPageOfUser(userId), "All page access of user has been fetched."));
        } catch (ResourceNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Cannot fetch list."));
        }
    }

    @GetMapping("/page-permissions/page/{pageId}")
    public ResponseEntity<ApiResponse<?>> getAllUsersOfPage(@PathVariable Long pageId){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userPagePermissionService.getAllUsersOfPage(pageId), "All users with access of this page has been fetched."));
        } catch (ResourceNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Cannot fetch list."));
        }
    }


}
