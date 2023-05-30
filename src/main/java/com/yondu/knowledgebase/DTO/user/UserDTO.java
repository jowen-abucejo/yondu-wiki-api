package com.yondu.knowledgebase.DTO.user;

import java.time.LocalDate;

public class UserDTO {
    public record GeneralInfo(Long id, String email, String username, String password, String firstName, String lastName, String status, LocalDate createdAt) {}
    public record GeneralResponse(Long id, String email, String username, String firstName, String lastName, String status, LocalDate createdAt) {}
    public record BaseResponse(Long id, String email) {}
    public record LoginRequest(String email, String password) {}
}
