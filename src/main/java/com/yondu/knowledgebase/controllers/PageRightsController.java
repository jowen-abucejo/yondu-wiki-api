package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.PageRightsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("/pages")
public class PageRightsController {

    private final PageRightsService pageRightsService;

    public PageRightsController(PageRightsService pageRightsService){
        this.pageRightsService = pageRightsService;
    }

/**
 * CONTROLLER CONTENTS:
 *
 * get all rights of a page
 * get all page rights separated by page
 *
 * add user to page right,
 * delete user to page right,
 * get page rights of a user
 * get all page rights separated by user
 *
 * add usergroup to page right
 * remove usergroup to page right
 * get page rights of a usergroup
 * get all page rights by user group
 *
 * fetch all users and user group that can access the page
 *
 * **/


    // PAGE RIGHTS
    /**
     * Fetching Page Rights of a Page
     * **/
    @GetMapping("/{pageId}/rights")
    public ResponseEntity<ApiResponse<?>> getPageRightsOfPage(@PathVariable Long pageId){
        Set<PageRightsDTO.PageRightBaseResponse> data = pageRightsService.getPageRightsOfPage(pageId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }

//    /**
//     * Fetching All Page Rights (separated per page)
//     * **/
//    @GetMapping("/rights")
//    public ResponseEntity<ApiResponse<?>> getAllPageRightsOfPage(){
////        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviewsByStatus(status,page,size);
//        PageRightsDTO.BaseResponse data = pageRightsService.getAllPageRightsOfPage();
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//
//    // ADDING USER TO PAGE RIGHTS
//    /**
//     * Add User to Page Right
//     * **/
//    @PostMapping("/{pageId}/rights/{rightsId}/add")
//    public ResponseEntity<ApiResponse<?>> addUserToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody String email ){
//        PageRightsDTO.BaseResponse data = pageRightsService.addUserToPageRights(pageId, rightsId, email);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//    /**
//     * Remove User to Page Right
//     * **/
//    @DeleteMapping("/{pageId}/rights/{rightsId}/remove")
//    public ResponseEntity<ApiResponse<?>> removeUserToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody String email ){
//        PageRightsDTO.BaseResponse data = pageRightsService.removeUserToPageRights(pageId, rightsId, email);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//    /**
//     * Get Page Rights of a User
//     * **/
//    @GetMapping("/users/{userId}/rights")
//    public ResponseEntity<ApiResponse<?>> getPageRightsOfUser(@PathVariable Long userId ){
//        PageRightsDTO.BaseResponse data = pageRightsService.getPageRightsOfUser(userId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//    /**
//     * Get All Page Rights separated by User
//     * **/
//    @GetMapping("/users/rights")
//    public ResponseEntity<ApiResponse<?>> getAllPageRightsOfUsers() {
////        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviewsByStatus(status,page,size);
//        PageRightsDTO.BaseResponse data = pageRightsService.getAllPageRightsOfUsers();
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//
//    // ADDING USERGROUP TO PAGE RIGHTS
//    /**
//     * Add UserGroup to Page Right
//     * **/
//    @PostMapping("/{pageId}/rights/{rightsId}/add")
//    public ResponseEntity<ApiResponse<?>> addUserGroupToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody Long groupId ){
//        PageRightsDTO.BaseResponse data = pageRightsService.addUserGroupToPageRights(pageId, rightsId, groupId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//    /**
//     * Remove User to Page Right
//     * **/
//    @DeleteMapping("/{pageId}/rights/{rightsId}/remove")
//    public ResponseEntity<ApiResponse<?>> removeUserGroupToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody Long groupId ){
//        PageRightsDTO.BaseResponse data = pageRightsService.removeUserGroupToPageRights(pageId, rightsId, groupId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//    /**
//     * Get Page Rights of a UserGroup
//     * **/
//    @GetMapping("/user-groups/{userGroupId}/rights")
//    public ResponseEntity<ApiResponse<?>> getPageRightsOfUserGroup(@PathVariable Long userGroupId ){
//        PageRightsDTO.BaseResponse data = pageRightsService.getPageRightsOfUserGroup(userGroupId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//    /**
//     * Get All Page Rights separated by UserGroup
//     * **/
//    @GetMapping("/user-groups/rights")
//    public ResponseEntity<ApiResponse<?>> getAllPageRightsOfUserGroups() {
////        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviewsByStatus(status,page,size);
//        PageRightsDTO.BaseResponse data = pageRightsService.getAllPageRightsOfUserGroups();
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }
//
//
//    // FETCHING DATA
//    /**
//     * Get All Users and UserGroup that can access the page
//     * **/
//    @GetMapping("/{pageId}/get-user-access")
//    public ResponseEntity<ApiResponse<?>> getAllUsersOfPage(@PathVariable Long pageId) {
////        PaginatedResponse<ReviewDTO.BaseResponse> review = reviewService.getAllReviewsByStatus(status,page,size);
//        PageRightsDTO.BaseResponse data = pageRightsService.getAllUsersOfPage(pageId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }



}
