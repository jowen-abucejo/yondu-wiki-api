package com.yondu.knowledgebase.DTO.directory.user_access;

import com.yondu.knowledgebase.DTO.directory.DirectoryDTO;
import com.yondu.knowledgebase.DTO.directory.DirectoryDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;

public class DirectoryUserAccessResponse {
    private Long id;
    private Long directoryId;
    private UserDTO.BaseResponse user;
    private DirectoryDTO.BaseResponse directory;
    private PermissionDTO.BaseResponse permission;

    public DirectoryUserAccessResponse() {
    }

    public DirectoryUserAccessResponse(DirectoryUserAccess directoryUserAccess) {
        this.id = directoryUserAccess.getId();
        this.directoryId = directoryUserAccess.getDirectory().getId();
        this.user = UserDTOMapper.mapToBaseResponse(directoryUserAccess.getUser());
        this.directory = DirectoryDTOMapper.mapToBaseResponse(directoryUserAccess.getDirectory());
        this.permission = PermissionDTOMapper.mapToBaseResponse(directoryUserAccess.getPermission());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public UserDTO.BaseResponse getUser() {
        return user;
    }

    public void setUser(UserDTO.BaseResponse user) {
        this.user = user;
    }

    public DirectoryDTO.BaseResponse getDirectory() {
        return directory;
    }

    public void setDirectory(DirectoryDTO.BaseResponse directory) {
        this.directory = directory;
    }

    public PermissionDTO.BaseResponse getPermission() {
        return permission;
    }

    public void setPermission(PermissionDTO.BaseResponse permission) {
        this.permission = permission;
    }
}
