package com.yondu.knowledgebase.DTO.permission;

public class PermissionDTO {
    public record BaseResponse(Long id, String name, String description, String category){}
    public record BaseRequest(Long id, String name) {}
}
