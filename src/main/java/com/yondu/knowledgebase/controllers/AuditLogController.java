package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTO;
import com.yondu.knowledgebase.services.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("audit-log")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }


    @GetMapping ("/my-activities")
    public ResponseEntity<ApiResponse<List<AuditLogDTO.BaseResponse>>> getAuditLogByUser() {
        List<AuditLogDTO.BaseResponse> auditLogs = auditLogService.getAuditLogsByUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(auditLogs, "User activity retrieved successfully"));
    }
}
