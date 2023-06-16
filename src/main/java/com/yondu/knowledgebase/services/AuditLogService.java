package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.AuditLog;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


}
