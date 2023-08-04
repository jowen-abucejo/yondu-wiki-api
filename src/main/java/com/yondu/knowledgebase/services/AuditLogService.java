package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTO;
import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTOMapper;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.entities.AuditLog;
import com.yondu.knowledgebase.entities.Role;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.repositories.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public PaginatedResponse<AuditLogDTO.BaseResponse> getAuditLogsByUser(
            String searchKey,
            String entityType,
            int page,
            int size) {
        // Check if logged-in user is admin
        if (!userIsAdmin()) {
            throw new AccessDeniedException();
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<AuditLog> auditLogPages = auditLogRepository.searchAuditLogs(searchKey, entityType, pageRequest);
        List<AuditLog> auditLogs = auditLogPages.getContent();

        List<AuditLogDTO.BaseResponse> auditLog = auditLogs.stream()
                .map(AuditLogDTOMapper::mapToBaseResponse)
                .collect(Collectors.toList());
        return new PaginatedResponse<>(auditLog, page, size, (long) auditLogPages.getTotalPages());
    }

    public List<AuditLogDTO.BaseResponse> getAuditLogByEntity(String entityType, Long entityId) {
        // Check if logged-in user is admin
        if (!userIsAdmin()) {
            throw new AccessDeniedException();
        }
        List<AuditLog> auditLogs = auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType,
                entityId);
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
