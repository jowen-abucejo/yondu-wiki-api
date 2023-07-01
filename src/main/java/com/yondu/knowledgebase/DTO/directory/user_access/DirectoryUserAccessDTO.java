package com.yondu.knowledgebase.DTO.directory.user_access;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;

import java.util.List;

public class DirectoryUserAccessDTO {

    public record UserAccess(String email, @JsonProperty Long permission_id){}
    public record UserAccessResult (UserAccess access, String result){}
}
