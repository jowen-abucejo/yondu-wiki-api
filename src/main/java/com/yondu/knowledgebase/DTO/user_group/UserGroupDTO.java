package com.yondu.knowledgebase.DTO.user_group;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class UserGroupDTO {
    public record GeneralResponse(String groupName, String description, Set<UserDTO.ShortRequest> users) {}
    public record UserGroupRequest(String groupName, String description, Set<UserDTO.ShortRequest> users) {}
    public record EditUsers(Set<UserDTO.ShortRequest> users){}
    public record AddPermisison(Long permissionId){}
}
