package com.yondu.knowledgebase.DTO.directory.user_access;

import com.yondu.knowledgebase.DTO.directory.DirectoryDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;

public class DirectoryUserAccessDTOMapper {
    public static DirectoryUserAccessDTO.BaseResponse mapToBaseResponse(DirectoryUserAccess directoryUserAccess) {
        return new DirectoryUserAccessDTO.BaseResponse(directoryUserAccess.getId(),
                UserDTOMapper.mapToShortResponse(directoryUserAccess.getUser()),
                PermissionDTOMapper.mapToBaseResponse(directoryUserAccess.getPermission()),
                DirectoryDTOMapper.mapToShortResponse(directoryUserAccess.getDirectory()));
    }
}
