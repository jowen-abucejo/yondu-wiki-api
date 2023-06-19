package com.yondu.knowledgebase.DTO.audit_log;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.AuditLog;

import java.time.Duration;
import java.time.LocalDateTime;

public class AuditLogDTOMapper {

    public static AuditLogDTO.BaseResponse mapToBaseResponse(AuditLog auditLog) {
        return new AuditLogDTO.BaseResponse(auditLog.getId(),
                UserDTOMapper.mapToGeneralResponse(auditLog.getUser()),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getAction(),
                convertTimestampToRelative(auditLog.getTimestamp()));

    }

    public static String convertTimestampToRelative(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);

        if (duration.toDays() > 0) {
            return duration.toDays() + " days ago";
        } else if (duration.toHours() > 0) {
            return duration.toHours() + " hours ago";
        } else if (duration.toMinutes() > 0) {
            return duration.toMinutes() + " minutes ago";
        } else {
            return "just now";
        }
    }
}
