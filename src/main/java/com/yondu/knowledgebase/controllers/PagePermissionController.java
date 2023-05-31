package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page_permission.user_access.UserPagePermissionDTO;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.UserPagePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "page-permission")
public class PagePermissionController {

    private final UserPagePermissionService userPagePermissionService;

    public PagePermissionController(UserPagePermissionService userPagePermissionService){
        this.userPagePermissionService = userPagePermissionService;
    }

    /**
     * Adding user roles in page permission roles
     * **/

    @PostMapping("/{permissionId}/add-user")
    public ResponseEntity<ApiResponse<?>> addUserToPageAccess(@PathVariable Long permissionId, @RequestBody UserPagePermissionDTO.AddUser userPagePermission){

        if(userPagePermission.pageId() == null || userPagePermission.userId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Incomplete Fields"));
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userPagePermissionService.addUserToPageAccess(permissionId, userPagePermission), "User has been successfully added in page permission."));
        } catch(DuplicateResourceException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        } catch (ResourceNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Cannot add user to page permission."));
        }
    }

    @PostMapping("/{permissionId}/remove-user")
    public ResponseEntity<ApiResponse<?>> removeUserToPageAccess(@PathVariable Long permissionId, @RequestBody UserPagePermissionDTO.AddUser userPagePermission){
        if(userPagePermission.pageId() == null || userPagePermission.userId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Incomplete Fields"));
        }

        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userPagePermissionService.removeUserToPageAccess(permissionId, userPagePermission), "User has been successfully removed in page permission."));
        } catch (ResourceNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Cannot add user to page permission."));
        }

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<?>> getAllPagePermissionOfUser(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userPagePermissionService.getAllPagePermissionOfUser(id), "All page access of user has been fetched."));
        } catch (ResourceNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Cannot fetch list."));
        }
    }

}
