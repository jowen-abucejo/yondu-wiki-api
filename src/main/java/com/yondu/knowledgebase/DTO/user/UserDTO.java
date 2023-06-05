package com.yondu.knowledgebase.DTO.user;

import com.yondu.knowledgebase.DTO.role.RoleDTO;

import java.time.LocalDate;
import java.util.Set;

public class UserDTO {
    public record GeneralInfo(Long id, String email, String username, String password, String firstName, String lastName, String status, LocalDate createdAt) {}
    public record GeneralResponse(Long id, String email, String username, String profilePhoto, String position, String firstName, String lastName, String status, LocalDate createdAt) {}
    public record BaseWithRoles(Long id, String email , String username, String firstName, String lastName, String status, LocalDate createdAt, Set<RoleDTO> roles) {}
    public record BaseResponse(Long id, String email, String username, String firstName, String lastName, String status, LocalDate createdAt) {}
    public record LoginRequest(String email, String password) {}
    public record ShortResponse(Long id, String email, String username, String firstName, String lastName){}
    public record ShortRequest(String email){}

    public record WithRolesRequest(Long id, String email, String password, String profilePhoto, String position, String username, String firstName, String lastName, String status, LocalDate createdAt, Set<RoleDTO> roles) {}
    public record WithRolesResponse(Long id, String email , String username, String profilePhoto, String position, String firstName, String lastName, String status, LocalDate createdAt, Set<RoleDTO> roles) {}


    public record ChangePassRequest(String oldPassword, String newPassword) {}

}
