package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.entities.Page;

import java.util.List;
import java.util.Set;

public interface PageRightsService {
    Set<PageRightsDTO.PageRightBaseResponse> getPageRightsOfPage(Long pageId);

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
