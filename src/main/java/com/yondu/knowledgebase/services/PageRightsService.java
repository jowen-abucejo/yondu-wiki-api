package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.entities.Page;

public interface PageRightsService {
    PageRightsDTO.GetPageRightResponse getPageRightsOfPage(Long pageId);

    PaginatedResponse<PageRightsDTO.GetPageRightResponse> getAllPageRightsOfPage(int page, int size);

    PageRightsDTO.GetUserPageRightBaseResponse addUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email);

    PageRightsDTO.GetUserPageRightBaseResponse removeUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email);

    PageRightsDTO.GetUserPageRightResponse getPageRightsOfUser(Long userId);

    PageRightsDTO.UserGroupBaseResponse addUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId);

    PageRightsDTO.UserGroupBaseResponse removeUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId);

    //
    // public Set<PageRightsDTO.BaseResponse> addUserToPageAccess(Long pageId,
    // PageRightsDTO.AddUser userPermission);
    //
    // public PageRightsDTO.BaseResponse removeUserToPageAccess(Long pageId,
    // PageRightsDTO.UserPermissionPair userPagePermission);
    //
    // public List<PageRightsDTO.BaseResponse> getAllPageOfUser(Long id);

    // public List<PageRightsDTO.BaseResponse> getAllUsersOfPage(Long id);

    public void createPageRights(Page page);
}

