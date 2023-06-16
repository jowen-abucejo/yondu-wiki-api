package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTO;
import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTOMapper;
import com.yondu.knowledgebase.entities.AuditLog;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void createAuditLog(User user, String entityType, Long entityId, String action) {
        AuditLog activity = new AuditLog();
        activity.setUser(user);
        activity.setEntityType(entityType);
        activity.setEntityId(entityId);
        activity.setAction(action);
        activity.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(activity);
    }

    public List<AuditLogDTO.BaseResponse> getAuditLogsByUser() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AuditLog> auditLogs = auditLogRepository.findByUserOrderByTimestampDesc(currentUser);
        List<AuditLogDTO.BaseResponse> auditLogResponse = new ArrayList<>();
        for (AuditLog auditLog : auditLogs) {
            AuditLogDTO.BaseResponse baseResponse = AuditLogDTOMapper.mapToBaseResponse(auditLog);
            auditLogResponse.add(baseResponse);
        }
        return auditLogResponse;

    }
}
