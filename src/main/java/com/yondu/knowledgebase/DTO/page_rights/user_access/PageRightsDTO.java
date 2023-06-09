package com.yondu.knowledgebase.DTO.page_rights.user_access;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page_rights.PageDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class PageRightsDTO {
    public record RightsPermissionResponse(Long rights_id, PermissionDTO.BaseResponse permissions){}

    // Page Rights
    public record GetPageRightResponse(PageDTO.BaseResponse page, Set<RightsPermissionResponse> page_rights){}

    // Add User
    public record AddUserRequest(String email){}
    public record UserBaseResponse(Long rights_id, UserDTO.ShortResponse user, PageDTO.BaseResponse page, PermissionDTO.BaseResponse permissions){}
    public record GetUserPageRightResponse(UserDTO.ShortResponse user, Set<GetPageRightResponse> user_page_rights){}
    public record GetUserPageRightBaseResponse(UserDTO.ShortResponse user, GetPageRightResponse user_page_rights){}

    // Add UserGroup
    public record AddUserGroupRequest(String usergroupId){}
    public record UserGroupBaseResponse(GroupDTO.ShortResponse group,GetPageRightResponse user_page_rights){}
    public record GetUserGroupPageRightResponse(GroupDTO.ShortResponse group, Set<GetPageRightResponse> user_page_rights){}

}
