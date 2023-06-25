package com.yondu.knowledgebase.DTO.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.rights.RightsDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.util.Set;

public class GroupDTO {
    public record BaseResponse(Long id, String name, String description, Boolean is_active, Set<UserDTO.ShortResponse> users) {}
    public record GroupRequest(String name, String description) {}
    public record UserRequest(String email){}
    public record AddPermission(Long permissionId){}
    public record AddRightsRequest(@JsonProperty("group_id") Long groupId){}
    public record ShortResponse (Long id, String name, String description){}

}
