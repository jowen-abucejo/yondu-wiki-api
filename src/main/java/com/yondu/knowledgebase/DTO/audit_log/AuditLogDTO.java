package com.yondu.knowledgebase.DTO.audit_log;



import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDateTime;

public class AuditLogDTO {

    public record BaseResponse(Long id,
                               UserDTO.GeneralResponse user,
                               String entityType,
                               Long entityId,
                               String action,
                               LocalDateTime timestamp){}

    public record GetRequest(String email) {}
}


