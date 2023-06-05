package com.yondu.knowledgebase.DTO.user;

import com.yondu.knowledgebase.DTO.role.RoleDTO;
import com.yondu.knowledgebase.entities.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserDTOMapper {
    public static UserDTO.BaseResponse mapToBaseResponse(User user) {
        return new UserDTO.BaseResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getStatus(), user.getCreatedAt());
    }

    public static UserDTO.BaseWithRoles mapToBaseWithRoles(User user) {
        Set<RoleDTO> roles = user.getRole()
                .stream()
                .map(RoleDTO::new)
                .collect(Collectors.toSet());

        return new UserDTO.BaseWithRoles(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getStatus(), user.getCreatedAt(), roles);
    }

    public static UserDTO.GeneralResponse mapToGeneralResponse(User user) {
        return new UserDTO.GeneralResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getStatus(), user.getCreatedAt());
    }

    public static UserDTO.ShortResponse mapToShortResponse(User user) {
        return new UserDTO.ShortResponse(user.getId(), user.getEmail(), user.getUsername(), user.getFirstName(), user.getLastName());
    }

    public static UserDTO.WithRolesResponse mapToWithRolesResponse(User user) {
        Set<RoleDTO> roles = user.getRole()
                .stream()
                .map(RoleDTO::new)
                .collect(Collectors.toSet());

        return new UserDTO.WithRolesResponse(user.getId(), user.getEmail(), user.getUsername(), user.getProfilePhoto(), user.getPosition(), user.getFirstName(), user.getLastName(), user.getStatus(), user.getCreatedAt(), roles);
    }
}
