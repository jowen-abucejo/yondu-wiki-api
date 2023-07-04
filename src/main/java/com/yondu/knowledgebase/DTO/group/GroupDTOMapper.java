package com.yondu.knowledgebase.DTO.group;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Group;

import java.util.stream.Collectors;

public class GroupDTOMapper {
    public static GroupDTO.BaseResponse mapToBaseResponse(Group group) {
        return new GroupDTO.BaseResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getDateCreated(),
                group.getDateModified(),
                UserDTOMapper.mapToGeneralResponse(group.getCreatedBy()),
                group.getActive(),
                group.getUsers().stream().map(UserDTOMapper::mapToGeneralResponse).collect(Collectors.toSet()));
    }

    public static GroupDTO.ShortResponse mapToShortResponse(Group group) {
        return new GroupDTO.ShortResponse(
                group.getId(),
                group.getName(),
                group.getDescription());
    }
}
