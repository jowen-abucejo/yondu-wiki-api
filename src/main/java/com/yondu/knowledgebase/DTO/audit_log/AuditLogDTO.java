package com.yondu.knowledgebase.DTO.audit_log;



import com.yondu.knowledgebase.DTO.user.UserDTO;

public class AuditLogDTO {

    public record BaseResponse(Long id,
                               UserDTO.GeneralResponse user,
                               String entityType,
                               Long entityId,
                               String action,
                               String timestamp){}

    public record GetRequest(String email) {}
}


