package com.yondu.knowledgebase.DTO.page_rights.user_access;

import com.yondu.knowledgebase.DTO.group.GroupDTOMapper;
import com.yondu.knowledgebase.DTO.page_rights.PageDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.PageRights;
import com.yondu.knowledgebase.entities.User;

import java.util.Set;

public class PageRightsDTOMapper {
    public static PageRightsDTO.RightsPermissionResponse mapToBaseResponse(PageRights pageRights) {
        return new PageRightsDTO.RightsPermissionResponse(pageRights.getId(),
                PermissionDTOMapper.mapToBaseResponse(pageRights.getPermission()));
    }
    public static PageRightsDTO.GetPageRightResponse mapToPageRightResponse(Page page, Set<PageRightsDTO.RightsPermissionResponse> page_rights) {
        return new PageRightsDTO.GetPageRightResponse(PageDTOMapper.mapToBaseResponse(page),
                page_rights);
    }

    public static PageRightsDTO.UserBaseResponse mapToAddPageRightResponse(User user, PageRights pageRights) {
        return new PageRightsDTO.UserBaseResponse(pageRights.getId(),
                UserDTOMapper.mapToShortResponse(user), PageDTOMapper.mapToBaseResponse(pageRights.getPage()),
                PermissionDTOMapper.mapToBaseResponse(pageRights.getPermission()));
    }

    public static PageRightsDTO.GetUserPageRightResponse mapToUserRightResponse(User user, Set<PageRightsDTO.GetPageRightResponse> user_rights) {
        return new PageRightsDTO.GetUserPageRightResponse(UserDTOMapper.mapToShortResponse(user),
                user_rights);
    }

    public static PageRightsDTO.GetUserPageRightBaseResponse mapToUserRightBaseResponse(User user, PageRightsDTO.GetPageRightResponse user_rights) {
        return new PageRightsDTO.GetUserPageRightBaseResponse(UserDTOMapper.mapToShortResponse(user),
                user_rights);
    }

    public static PageRightsDTO.UserGroupBaseResponse mapToAddPageRightResponse(Group group, PageRightsDTO.GetPageRightResponse user_rights) {
        return new PageRightsDTO.UserGroupBaseResponse(GroupDTOMapper.mapToShortResponse(group),
                user_rights);
    }

}
