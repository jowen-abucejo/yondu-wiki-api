package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.AuditLog;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByTimestampDesc(User user);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:searchKey = '' OR " +
            "(a.action LIKE %:searchKey% OR " +
            "a.user.email LIKE %:searchKey% OR " +
            "a.user.firstName LIKE %:searchKey% OR " +
            "a.user.lastName LIKE %:searchKey%)) " +
            "AND (:entityType = '' OR a.entityType = :entityType) " +
            "ORDER BY a.timestamp DESC")
    Page<AuditLog> searchAuditLogs(
            @Param("searchKey") String searchKey,
            @Param("entityType") String entityType,
            Pageable pageable);
}
