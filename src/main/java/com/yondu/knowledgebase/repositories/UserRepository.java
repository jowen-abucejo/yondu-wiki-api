package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM users u WHERE email = ?1 AND status = 'ACT'")
    Optional<User> fetchUserByEmail(String email);

    @Query("SELECT u FROM users u WHERE email = ?1 AND status = 'INA'")
    Optional<User> fetchUserInactiveByEmail(String email);

    @Query("SELECT u FROM users u WHERE email = ?1 AND status = 'ACT'")
    User getUserByEmail(String email);

    @Query("UPDATE users SET status = 'INA' WHERE email = ?1 AND status = 'ACT'")
    void deactivateUserByEmail(String email);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM users u WHERE u.email LIKE ?1 OR u.username LIKE ?1 OR u.firstName LIKE ?1 OR u.lastName LIKE ?1")
    Page<User> findAll(String searchKey, Pageable pageable);

    Optional<User> findByEmail(String email);

    User findByFirstNameOrLastName(String firstName, String lastName);

    @Query("SELECT u FROM users u JOIN u.role r JOIN r.permissions p WHERE p.name = 'CONTENT_APPROVAL'")
    Set<User> findUsersWithContentApprovalPermission();

    @Query(nativeQuery = true, value = """
            SELECT
                (EXISTS(SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN user_role ur ON u.id = ur.user_id
                        LEFT JOIN role_permission rp ON rp.role_id = ur.role_id
                        LEFT JOIN permission p ON rp.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId)
                    AND (EXISTS( SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN user_page_access upa ON u.id = upa.user_id
                        LEFT JOIN permission p ON upa.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId
                                AND upa.page_id = :pageId)
                    OR EXISTS(SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN group_users gu ON u.id = gu.user_id
                        LEFT JOIN group_page_access gpa ON gu.group_id = gpa.group_id
                        LEFT JOIN permission p ON gpa.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId
                                AND gpa.page_id = :pageId))) AS isGranted
                                                        """)
    public Long userHasPagePermission(Long userId, Long pageId, String permission);

    @Query(nativeQuery = true, value = """
            SELECT
                (EXISTS(SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN user_role ur ON u.id = ur.user_id
                        LEFT JOIN role_permission rp ON rp.role_id = ur.role_id
                        LEFT JOIN permission p ON rp.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId)
                    AND EXISTS(SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN directory_user_access dua ON u.id = dua.user_id
                        LEFT JOIN permission p ON dua.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId
                                AND dua.directory_id = :directoryId)) AS isGranted
                                                """)
    public Long userHasDirectoryPermission(Long userId, Long directoryId, String permission);

    @Query("SELECT u FROM users u JOIN u.role r WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %?1% AND u.status = ?2 AND r.roleName = ?3")
    Page<User> findAllByFullNameAndStatusAndRole(String fullName, String statusFilter, String roleFilter,
            Pageable pageable);

    @Query("SELECT u FROM users u JOIN u.role r WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %?1% AND u.status = ?2")
    Page<User> findAllByFullNameAndStatus(String fullName, String statusFilter, Pageable pageable);

    @Query("SELECT u FROM users u JOIN u.role r WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %?1% AND r.roleName = ?2")
    Page<User> findAllByFullNameAndRole(String fullName, String roleFilter, Pageable pageable);

    @Query("SELECT u FROM users u JOIN u.role r WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %?1%")
    Page<User> findAllByFullName(String fullName, Pageable pageable);

    @Query("""
            SELECT u FROM users u
            JOIN u.role r
            JOIN r.permissions p
            WHERE (u.firstName LIKE ?1
            OR u.lastName LIKE ?1
            OR u.email LIKE ?1)
            AND p.id = ?2
            """)
    Page<User> findAllByFullNameAndHasPermission(String searchKey, Long permissionId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT
                p.id AS pageId,
                pv.id AS pageVersionId,
                u.id AS userId,
                ws.id AS nextWorkflowStepId,
                ws.step AS nextStep,
                COALESCE(rv3.pendingReviewCount, 0) AS pendingReviewCount
            FROM
                knowledge_base.page_version pv
                    LEFT JOIN
                knowledge_base.page p ON pv.page_id = p.id
                    LEFT JOIN
                knowledge_base.directory d ON d.id = p.directory_id
                    LEFT JOIN
                knowledge_base.workflow w ON d.id = w.directory_id
                    LEFT JOIN
                knowledge_base.workflow_step ws ON w.id = ws.workflow_id
                    LEFT JOIN
                knowledge_base.workflow_step_approver wsp ON wsp.workflow_step_id = ws.id
                    LEFT JOIN
                knowledge_base.users u ON wsp.approver_id = u.id
                    LEFT JOIN
                (SELECT
                    page_version_id, COUNT(*) AS pendingReviewCount
                FROM
                    knowledge_base.review r3
                WHERE
                    status = 'PENDING'
                GROUP BY page_version_id) rv3 ON rv3.page_version_id = pv.id
            WHERE
                u.id = :userId AND p.id=:pageId AND pv.id = :versionId
                    AND ws.step = ((SELECT
                        COALESCE(MAX(ws2.step), 0)
                    FROM
                        knowledge_base.review r
                            LEFT JOIN
                        knowledge_base.workflow_step ws2 ON ws2.id = r.workflow_step_id
                    WHERE
                        r.page_version_id = :versionId) + 1)
            		ANd rv3.pendingReviewCount > 0
                                    """)
    /**
     * Get a map of ids for user, page, version and next workflow step
     * only if both (1) user with the given id is approver for the next step
     * and (2) page version is already submitted as 'PENDING'
     * 
     * @param userId
     * @param pageId
     * @param versionId
     * @return a map containing the userId, pageId, pageVersionId,
     *         nextWorkflowStepId, nextStep, and pendingReviewCount
     */
    public Map<String, Long> checkUserPageApprovalPermissionAndGetNextWorkflowStep(Long userId, Long pageId,
            Long versionId);

}
