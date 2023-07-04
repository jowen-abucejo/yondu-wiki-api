package com.yondu.knowledgebase.controllers;


import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.group.GroupDTO;
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

    // ADDING USER TO PAGE RIGHTS
    /**
     * Add User to Page Right
     * **/
    @PostMapping("/{pageId}/rights/{rightsId}/add-user")
    public ResponseEntity<ApiResponse<PageRightsDTO.GetUserPageRightBaseResponse>> addUserToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody PageRightsDTO.AddUserRequest email ){
        PageRightsDTO.GetUserPageRightBaseResponse data = pageRightsService.addUserToPageRights(pageId, rightsId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User has been added to this Page successfully"));
    }


    /**
     * Remove User to Page Right
     * **/
    @PostMapping("/{pageId}/rights/{rightsId}/remove-user")
    public ResponseEntity<ApiResponse<PageRightsDTO.GetUserPageRightBaseResponse>> removeUserToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody PageRightsDTO.AddUserRequest email ){
        PageRightsDTO.GetUserPageRightBaseResponse data = pageRightsService.removeUserToPageRights(pageId, rightsId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User has been modified to this Page successfully"));
    }




    // ADDING USERGROUP TO PAGE RIGHTS
    /**
     * Add UserGroup to Page Right
     * **/
    @PostMapping("/{pageId}/rights/{rightsId}/add-usergroup")
    public ResponseEntity<ApiResponse<PageRightsDTO.UserGroupBaseResponse>> addUserGroupToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody GroupDTO.AddRightsRequest groupId ){
        PageRightsDTO.UserGroupBaseResponse data = pageRightsService.addUserGroupToPageRights(pageId, rightsId, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User Group has been added to this Page successfully"));
    }


    /**
     * Remove User to Page Right
     * **/
    @PostMapping("/{pageId}/rights/{rightsId}/remove-usergroup")
    public ResponseEntity<ApiResponse<PageRightsDTO.UserGroupBaseResponse>> removeUserGroupToPageRights(@PathVariable Long pageId, @PathVariable Long rightsId,@RequestBody GroupDTO.AddRightsRequest groupId ){
        PageRightsDTO.UserGroupBaseResponse data = pageRightsService.removeUserGroupToPageRights(pageId, rightsId, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "User Group has been modified to this Page successfully"));
    }




    // FETCHING DATA


    /**
     * Get All Pages (and page rights) a User can access
     * **/
    @GetMapping("/users/{userId}/rights")
    public ResponseEntity<ApiResponse<PageRightsDTO.GetUserPageRightResponse>> getRightsOfUser(@PathVariable Long userId ){
        PageRightsDTO.GetUserPageRightResponse data = pageRightsService.getPageRightsOfUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "All Pages the User can access has been fetched successfully"));
    }


    /**
     * Get All Pages (and Page Rights) a UserGroup can access
     * **/
    @GetMapping("/user-groups/{userGroupId}/rights")
    public ResponseEntity<ApiResponse<PageRightsDTO.GetUserGroupPageRightResponse>> getPageRightsOfUserGroup(@PathVariable Long userGroupId ){
        PageRightsDTO.GetUserGroupPageRightResponse data = pageRightsService.getPageRightsOfUserGroup(userGroupId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "All Pages the User Group can access has been fetched successfully"));
    }


    /**
     * Get All Users and UserGroup that can access the page
     * **/
    @GetMapping("/{pageId}/get-user-access")
    public ResponseEntity<ApiResponse<PageRightsDTO.GetPageRightOfPageResponse>> getAllUsersOfPage(@PathVariable Long pageId) {
        PageRightsDTO.GetPageRightOfPageResponse data = pageRightsService.getAllUsersOfPage(pageId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, "All User and User Groups with access to this Page has been fetched successfully"));
    }


}
