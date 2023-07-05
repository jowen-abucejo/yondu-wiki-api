package com.yondu.knowledgebase.DTO.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class GroupDTO {
    public record BaseResponse(Long id, String name, String description, LocalDateTime dateCreated, LocalDateTime dateModified, UserDTO.GeneralResponse createdBy, Boolean is_active, Set<UserDTO.GeneralResponse> members, Set<PermissionDTO.BaseResponse> permissions) {}
    public record GroupRequest(String name, String description, List<Long> members, List<Long> permissions) {}
    public record UpdateGroupRequest(String name, String description, @JsonProperty("is_active") boolean isActive, List<Long> members, List<Long> permissions){}
    public record UserRequest(String email){}
    public record AddPermission(Long permissionId){}
    public record AddRightsRequest(@JsonProperty("id") Long groupId){}
    public record ShortResponse (Long id, String name, String description){}

}
