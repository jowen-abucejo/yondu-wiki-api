package com.yondu.knowledgebase.DTO.audit_log;

import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.AuditLog;

public class AuditLogDTOMapper {

    public static AuditLogDTO.BaseResponse mapToBaseResponse(AuditLog auditLog) {
        return new AuditLogDTO.BaseResponse(auditLog.getId(),
                UserDTOMapper.mapToGeneralResponse(auditLog.getUser()),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getAction(),
                auditLog.getTimestamp());

    }


}
