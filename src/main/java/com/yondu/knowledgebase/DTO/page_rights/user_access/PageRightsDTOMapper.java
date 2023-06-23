package com.yondu.knowledgebase.DTO.page_rights.user_access;

import com.yondu.knowledgebase.DTO.group.GroupDTOMapper;
import com.yondu.knowledgebase.DTO.page_rights.PageDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.*;

import java.util.Set;

public class PageRightsDTOMapper {
    public static PageRightsDTO.RightsPermissionResponse mapToBaseResponse(UserPageAccess pageRights) {
        return new PageRightsDTO.RightsPermissionResponse(pageRights.getId(),
                PermissionDTOMapper.mapToBaseResponse(pageRights.getPermission()));
    }

    public static PageRightsDTO.RightsPermissionResponse mapToBaseResponse(GroupPageAccess pageRights) {
        return new PageRightsDTO.RightsPermissionResponse(pageRights.getId(),
                PermissionDTOMapper.mapToBaseResponse(pageRights.getPermission()));
    }

    public static PageRightsDTO.GetPageRightResponse mapToPageRightResponse(Page page, Set<PageRightsDTO.RightsPermissionResponse> pageRights) {
        return new PageRightsDTO.GetPageRightResponse(PageDTOMapper.mapToBaseResponse(page),
                pageRights);
    }

    public static PageRightsDTO.GetUserPageRightResponse mapToUserRightResponse(User user, Set<PageRightsDTO.GetPageRightResponse> userRights) {
        return new PageRightsDTO.GetUserPageRightResponse(UserDTOMapper.mapToShortResponse(user),
                userRights);
    }


    public static PageRightsDTO.GetUserPageRightBaseResponse mapToUserRightBaseResponse(User user, PageRightsDTO.GetPageRightResponse userRights) {
        return new PageRightsDTO.GetUserPageRightBaseResponse(UserDTOMapper.mapToGeneralResponse(user),
                userRights);
    }


    public static PageRightsDTO.UserGroupBaseResponse mapToAddPageRightResponse(Group group, PageRightsDTO.GetPageRightResponse userRights) {
        return new PageRightsDTO.UserGroupBaseResponse(GroupDTOMapper.mapToShortResponse(group),
                userRights);
    }


    public static PageRightsDTO.GetUserGroupPageRightResponse mapToUserGroupRightResponse(Group group, Set<PageRightsDTO.GetPageRightResponse> userRights) {
        return new PageRightsDTO.GetUserGroupPageRightResponse(GroupDTOMapper.mapToShortResponse(group),
                userRights);
    }

    public static PageRightsDTO.GetPageRightOfGroups mapToPageRightOfGroups(Group group, Set<PageRightsDTO.RightsPermissionResponse> userRights) {
        return new PageRightsDTO.GetPageRightOfGroups(GroupDTOMapper.mapToShortResponse(group),
                userRights);
    }

    public static PageRightsDTO.GetPageRightOfUsers mapToPageRightOfUsers(User user, Set<PageRightsDTO.RightsPermissionResponse> userRights) {
        return new PageRightsDTO.GetPageRightOfUsers(UserDTOMapper.mapToGeneralResponse(user),
                userRights);
    }

    public static PageRightsDTO.GetPageRightOfPageResponse mapToPageRightsOfPageResponse(Page page, Set<PageRightsDTO.GetPageRightOfGroups> userGroups, Set<PageRightsDTO.GetPageRightOfUsers> users) {
        return new PageRightsDTO.GetPageRightOfPageResponse(PageDTOMapper.mapToBaseResponse(page),
                userGroups, users);
    }


}
