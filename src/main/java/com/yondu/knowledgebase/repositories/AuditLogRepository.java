package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
