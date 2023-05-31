package com.yondu.knowledgebase.DTO.group;

import com.yondu.knowledgebase.DTO.rights.RightsDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Collection;
import java.util.Set;

public class GroupDTO {
    public record BaseResponse(String name, String description, Set<UserDTO.ShortResponse> users, Set<RightsDTO.BaseResponse> rights) {}
    public record GeneralResponse(String name, String description, Set<UserDTO.ShortRequest> users) {}
    public record UserGroupRequest(String name, String description, Set<UserDTO.ShortRequest> users) {}
    public record EditUsersRequest(Set<UserDTO.ShortRequest> users){}
    public record EditGroupRequest(String name, String description){}
    public record AddPermission(Long permissionId){}
}
