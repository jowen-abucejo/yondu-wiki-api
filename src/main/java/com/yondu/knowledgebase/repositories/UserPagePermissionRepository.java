package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserPagePermission;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserPagePermissionRepository extends JpaRepository<UserPagePermission, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE UserPagePermission up SET up.isActive = :isActive, up.lastModified = :lastModified WHERE up.page = :pageId AND " +
            "up.permission = :permissionId AND up.user = :userId AND up.isActive = :origStatus ")
    void modifyUserPermission(Boolean isActive, LocalDateTime lastModified , Page pageId, Permission permissionId, User userId, Boolean origStatus);

    Optional<UserPagePermission>findByPageAndPermissionAndUserAndIsActive(Page page, Permission permission, User user, Boolean isActive);

    List<UserPagePermission> findAllByUserAndIsActive(User user, boolean b);
}
