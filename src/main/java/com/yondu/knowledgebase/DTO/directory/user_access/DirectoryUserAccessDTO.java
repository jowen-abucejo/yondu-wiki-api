package com.yondu.knowledgebase.DTO.directory.user_access;

import com.yondu.knowledgebase.DTO.directory.DirectoryDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

public class DirectoryUserAccessDTO {
    public record BaseResponse(Long id, UserDTO.ShortResponse user, PermissionDTO.BaseResponse permission, DirectoryDTO.ShortResponse directory){}
    public record AddRequest(Long userId, Long permissionId) {}
}
