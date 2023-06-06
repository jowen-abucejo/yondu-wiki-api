package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PageRightsRepository extends JpaRepository<PageRights, Long> {

//    @Modifying
//    @Transactional
//    @Query("UPDATE UserPagePermission up SET up.isActive = :isActive, up.lastModified = :lastModified WHERE up.page = :pageId AND " +
//            "up.permission = :permissionId AND up.user = :userId AND up.isActive = :origStatus ")
//    void modifyUserPermission(Boolean isActive, LocalDateTime lastModified , Page pageId, Permission permissionId, User userId, Boolean origStatus);
//
//    Optional<UserPagePermission> findByPageAndPermissionAndUserAndIsActive(Page page, Permission permission, User user, Boolean isActive);
//
//    @Query("SELECT DISTINCT up FROM UserPagePermission up WHERE up.user = :user AND up.isActive = :isActive GROUP BY up.page, up.permission")
//    List<UserPagePermission> findAllByUserAndIsActiveGroupByPage(User user, Boolean isActive);
//
//    @Query("SELECT DISTINCT up FROM UserPagePermission up WHERE up.page = :page AND up.isActive = :isActive GROUP BY up.user, up.permission")
//    List<UserPagePermission> findAllByPageAndIsActiveGroupByUser(Page page, Boolean isActive);

    Set<PageRights> findAllByPage(Page page);
}
