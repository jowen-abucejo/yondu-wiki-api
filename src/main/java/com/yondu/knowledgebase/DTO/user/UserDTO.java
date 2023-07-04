package com.yondu.knowledgebase.DTO.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yondu.knowledgebase.DTO.role.RoleDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class UserDTO {
    public record GeneralInfo(Long id, String email, String username, String password, String firstName, String lastName, String status, LocalDate createdAt) {}
    public record GeneralResponse(Long id, String email, String username, @JsonProperty("profile_photo") String profilePhoto, String position, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName, String status, @JsonProperty("created_at") LocalDate createdAt) {}
    public record BaseWithRoles(Long id, String email , String username, String firstName, String lastName, String status, LocalDate createdAt, Set<RoleDTO> roles) {}
    public record BaseResponse(Long id, String email, String username, String firstName, String lastName, String status, LocalDate createdAt) {}
    public record LoginRequest(String email, String password) {}
    public record ShortResponse(Long id, String email, String username, String firstName, String lastName){}
    public record ShortRequest(String email){}

    public record WithRolesRequest(Long id, String email, String password, @JsonProperty("profile_photo") String profilePhoto, String position, String username, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName, String status, @JsonProperty("created_at") LocalDate createdAt, Set<RoleDTO> roles) {}
    public record CreateUserRequest(Long id, String email, @JsonProperty("profile_photo") String profilePhoto, String position, String username, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName, String status, @JsonProperty("created_at") LocalDate createdAt, Set<RoleDTO> roles) {}

    public record WithRolesResponse(Long id, String email , String username, String profilePhoto, String position, String firstName, String lastName, String status, LocalDate createdAt, Set<RoleDTO> roles, LocalDateTime passwordExpiration) {}


    public record ChangePassRequest(@JsonProperty("old_password") String oldPassword, @JsonProperty("new_password") String newPassword) {}

    public record ChangePhotoRequest(String path) {}

    public record ApproverResponse (Long id, String email, String username,@JsonProperty("profile_photo") String profilePhoto, String position, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName) {}

    public record ChangePassRequestV2(@JsonProperty("new_password") String newPassword) {}
}
