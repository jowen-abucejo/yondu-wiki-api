package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.AuditLog;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByTimestampDesc(User user);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);
}
