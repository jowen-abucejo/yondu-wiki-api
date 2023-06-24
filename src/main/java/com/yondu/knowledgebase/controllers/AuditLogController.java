package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.audit_log.AuditLogDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.review.ReviewDTO;
import com.yondu.knowledgebase.services.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }


    @GetMapping("/get-activities")
    public ResponseEntity<?> getAuditLogByUser(
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(defaultValue = "") String entityType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<AuditLogDTO.BaseResponse> auditLogs = auditLogService.getAuditLogsByUser(searchKey, entityType, page, size);

        ApiResponse apiResponse = ApiResponse.success(auditLogs, "Retrieved audit logs successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping ("/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<List<AuditLogDTO.BaseResponse>>> getAuditLogByContent(@PathVariable("entityType") String entityType, @PathVariable("entityId") Long entityId) {
        List<AuditLogDTO.BaseResponse> auditLogs = auditLogService.getAuditLogByEntity(entityType,entityId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(auditLogs, "Content activity retrieved successfully"));
    }
}
