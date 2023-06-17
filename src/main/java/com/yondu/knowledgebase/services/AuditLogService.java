package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTO;
import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTOMapper;
import com.yondu.knowledgebase.entities.AuditLog;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.repositories.AuditLogRepository;
import com.yondu.knowledgebase.repositories.RoleRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;

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

    public List<AuditLogDTO.BaseResponse> getAuditLogsByUser(String email) {
        // Check if logged-in user is admin
        if (!userIsAdmin()) {
            throw new AccessDeniedException();
        }

        User currentUser = userRepository.getUserByEmail(email);
        List<AuditLog> auditLogs = auditLogRepository.findByUserOrderByTimestampDesc(currentUser);
        List<AuditLogDTO.BaseResponse> auditLogResponse = new ArrayList<>();
        for (AuditLog auditLog : auditLogs) {
            AuditLogDTO.BaseResponse baseResponse = AuditLogDTOMapper.mapToBaseResponse(auditLog);
            auditLogResponse.add(baseResponse);
        }
        return auditLogResponse;

    }
    public List<AuditLogDTO.BaseResponse> getAuditLogByEntity(String entityType, Long entityId) {
      // Check if logged-in user is admin
        if (!userIsAdmin()) {
            throw new AccessDeniedException();
        }
        List<AuditLog> auditLogs = auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType,entityId);
        List<AuditLogDTO.BaseResponse> auditLogResponse = new ArrayList<>();
        for (AuditLog auditLog : auditLogs) {
            AuditLogDTO.BaseResponse baseResponse = AuditLogDTOMapper.mapToBaseResponse(auditLog);
            auditLogResponse.add(baseResponse);
        }
        return auditLogResponse;
    }
    public boolean userIsAdmin() {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (Role userRole : authenticatedUser.getRole()) {
            if (userRole.getRoleName().equals("Administrator")) {
                return true;
            }
        }
        return false;
    }
}
