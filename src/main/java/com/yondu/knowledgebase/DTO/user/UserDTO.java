package com.yondu.knowledgebase.DTO.user;

public class UserDTO {
    public record BaseResponse(Long id, String email) {}
    public record LoginRequest(String email, String password) {}
}
