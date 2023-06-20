package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.entities.Page;

public interface PageRightsService {

    PageRightsDTO.GetUserPageRightBaseResponse addUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email);

    PageRightsDTO.GetUserPageRightBaseResponse removeUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email);

    PageRightsDTO.GetUserPageRightResponse getPageRightsOfUser(Long userId);

    PageRightsDTO.UserGroupBaseResponse addUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId);

    PageRightsDTO.UserGroupBaseResponse removeUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId);

    public void createPageRights(Page page);

    PageRightsDTO.GetUserGroupPageRightResponse getPageRightsOfUserGroup(Long userGroupId);

    PageRightsDTO.GetPageRightOfPageResponse getAllUsersOfPage(Long pageId);}

