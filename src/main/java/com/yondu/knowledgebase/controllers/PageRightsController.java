package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.services.PageRightsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pages")
public class PageRightsController {

    private final PageRightsService pageRightsService;

    public PageRightsController(PageRightsService pageRightsService){
        this.pageRightsService = pageRightsService;
    }

    // PAGE RIGHTS
    /**
     * Fetching Page Rights of a Page
     * **/
    @GetMapping("/{pageId}/rights")
    public ResponseEntity<ApiResponse<?>> getPageRightsOfPage(@PathVariable Long pageId){
        PageRightsDTO.GetPageRightResponse data = pageRightsService.getPageRightsOfPage(pageId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }

    /**
     * Fetching All Page Rights (separated per page)
     * **/
    @GetMapping("/rights")
    public ResponseEntity<ApiResponse<?>> getAllPageRightsOfPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        PaginatedResponse<PageRightsDTO.GetPageRightResponse> data = pageRightsService.getAllPageRightsOfPage(page, size);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }


    // ADDING USER TO PAGE RIGHTS
    /**
     * Add User to Page Right
     * **/
    @PostMapping("/{pageId}/rights/{rightsId}/add-user")
    public ResponseEntity<ApiResponse<?>> addUserToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody PageRightsDTO.AddUserRequest email ){
        PageRightsDTO.GetUserPageRightBaseResponse data = pageRightsService.addUserToPageRights(pageId, rightsId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }

    /**
     * Remove User to Page Right
     * **/
    @DeleteMapping("/{pageId}/rights/{rightsId}/remove-user")
    public ResponseEntity<ApiResponse<?>> removeUserToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody PageRightsDTO.AddUserRequest email ){
        PageRightsDTO.GetUserPageRightBaseResponse data = pageRightsService.removeUserToPageRights(pageId, rightsId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }

    //get page and the rights a user have


    // ADDING USERGROUP TO PAGE RIGHTS
    /**
     * Add UserGroup to Page Right
     * **/
    @PostMapping("/{pageId}/rights/{rightsId}/add-usergroup")
    public ResponseEntity<ApiResponse<?>> addUserGroupToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody GroupDTO.AddRightsRequest groupId ){
        PageRightsDTO.UserGroupBaseResponse data = pageRightsService.addUserGroupToPageRights(pageId, rightsId, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }

    /**
     * Remove User to Page Right
     * **/
    @DeleteMapping("/{pageId}/rights/{rightsId}/remove-usergroup")
    public ResponseEntity<ApiResponse<?>> removeUserGroupToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody GroupDTO.AddRightsRequest groupId ){
        PageRightsDTO.UserGroupBaseResponse data = pageRightsService.removeUserGroupToPageRights(pageId, rightsId, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }

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

    /**
     * Get All Pages (and page rights) a User can access
     * **/
    @GetMapping("/users/{userId}/rights")
    public ResponseEntity<ApiResponse<?>> getRightsOfUser(@PathVariable Long userId ){
        PageRightsDTO.GetUserPageRightResponse data = pageRightsService.getPageRightsOfUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
    }

//    /**
//     * Get All Pages (and Page Rights) a UserGroup can access
//     * **/
//    @GetMapping("/user-groups/{userGroupId}/rights")
//    public ResponseEntity<ApiResponse<?>> getPageRightsOfUserGroup(@PathVariable Long userGroupId ){
//        PageRightsDTO.BaseResponse data = pageRightsService.getPageRightsOfUserGroup(userGroupId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Permission Rights of Page has been fetched successfully"));
//    }



}
