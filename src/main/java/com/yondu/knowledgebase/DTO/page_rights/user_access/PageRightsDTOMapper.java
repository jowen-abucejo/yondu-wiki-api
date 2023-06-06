package com.yondu.knowledgebase.DTO.page_rights.user_access;

import com.yondu.knowledgebase.DTO.page_rights.PageDTO;
import com.yondu.knowledgebase.DTO.page_rights.PageDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.PageRights;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PageRightsDTOMapper {
    public static PageRightsDTO.BaseResponse mapToBaseResponse(PageRights pageRights, User user) {
        return new PageRightsDTO.BaseResponse(pageRights.getId(),
                UserDTOMapper.mapToShortResponse(user),
                PermissionDTOMapper.mapToBaseResponse(pageRights.getPermission()),
                PageDTOMapper.mapToBaseResponse(pageRights.getPage()));
    }
    public static PageRightsDTO.PageRightBaseResponse mapToPageRightResponse(PageRights pageRights) {
        return new PageRightsDTO.PageRightBaseResponse(pageRights.getId(),
                PermissionDTOMapper.mapToBaseResponse(pageRights.getPermission()),
                PageDTOMapper.mapToBaseResponse(pageRights.getPage()));
    }

}
