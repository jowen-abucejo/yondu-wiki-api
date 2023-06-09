package com.yondu.knowledgebase.DTO.group;

import com.yondu.knowledgebase.DTO.rights.RightsDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class GroupDTO {
    public record BaseResponse(Long id, String name, String description, Set<UserDTO.ShortResponse> users, Set<RightsDTO.BaseResponse> rights) {}
    public record GroupRequest(String name, String description) {}
    public record UserRequest(String email){}
    public record AddPermission(Long permissionId){}
    public record AddRightsRequest(Long groupId){}
    public record ShortResponse (Long id, String name, String description){}

}
