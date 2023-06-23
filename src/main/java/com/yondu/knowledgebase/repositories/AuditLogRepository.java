package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.AuditLog;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByTimestampDesc(User user);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

    @Query("SELECT a FROM AuditLog a WHERE (a.entityType LIKE %?1% OR a.action LIKE %?1%) AND a.user = ?2 ORDER BY a.timestamp DESC")
    Page<AuditLog> findByEntityTypeOrActionAndUserOrderByTimestampDesc(String searchKey, User user, Pageable pageable);
}
