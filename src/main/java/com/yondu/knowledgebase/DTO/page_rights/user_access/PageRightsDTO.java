package com.yondu.knowledgebase.DTO.page_rights.user_access;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page_rights.PageDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class PageRightsDTO {
    public record RightsPermissionResponse(Long rightsId, PermissionDTO.BaseResponse permissions){}


    // Page Rights
    public record GetPageRightResponse(PageDTO.BaseResponse page, Set<RightsPermissionResponse> pageRights){}


    // For Users
    public record AddUserRequest(String email){}
    public record GetUserPageRightResponse(UserDTO.ShortResponse user, Set<GetPageRightResponse> userPageRights){}
    public record GetUserPageRightBaseResponse(UserDTO.ShortResponse user, GetPageRightResponse userPageRights){}


    // For UserGroups
    public record UserGroupBaseResponse(GroupDTO.ShortResponse group,GetPageRightResponse userPageRights){}
    public record GetUserGroupPageRightResponse(GroupDTO.ShortResponse group, Set<GetPageRightResponse> userPageRights){}


    // Get Users and UserGroups
    public record GetPageRightOfPageResponse(PageDTO.BaseResponse page, Set<PageRightsDTO.GetPageRightOfGroups> userGroups, Set<PageRightsDTO.GetPageRightOfUsers> users){}
    public record GetPageRightOfGroups(GroupDTO.ShortResponse group, Set<RightsPermissionResponse> userPageRights){}
    public record GetPageRightOfUsers(UserDTO.ShortResponse user, Set<RightsPermissionResponse> userPageRights){}

}
