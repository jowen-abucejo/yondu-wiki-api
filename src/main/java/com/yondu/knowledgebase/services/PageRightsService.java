package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.entities.Page;

import java.util.Set;

public interface PageRightsService {

    PageRightsDTO.GetUserPageRightBaseResponse addUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email);

    PageRightsDTO.GetUserPageRightBaseResponse removeUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email);

    PageRightsDTO.GetUserPageRightResponse getPageRightsOfUser(Long userId);

    PageRightsDTO.UserGroupBaseResponse addUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId);

    PageRightsDTO.UserGroupBaseResponse removeUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId);

    public void createPageRights(Page page);

    PageRightsDTO.GetUserGroupPageRightResponse getPageRightsOfUserGroup(Long userGroupId);

    PageRightsDTO.GetPageRightOfPageResponse getAllUsersOfPage(Long pageId);

    PageRightsDTO.GetPageRightResponse getPageRightsOfUserToPage(Long pageId);

    void checkUserHasRights(Long pageId, Set<PermissionDTO.BaseRequest> permissions);

    void checkIfOtherUserHasRights(Long pageId, Long userId, Set<PermissionDTO.BaseRequest> permissions);

    void checkIfGroupHasRights(Long pageId, Long groupId, Set<PermissionDTO.BaseRequest> permissions);
}

