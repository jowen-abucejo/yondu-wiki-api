package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.User;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            SELECT EXISTS(
                SELECT
                    pr20.name
                FROM
                    (
                        SELECT p.id, p.directory_id
                        FROM page p
                        WHERE
                        p.id = :pageId
                    ) p20 CROSS JOIN
                    (SELECT id FROM users WHERE id=:userId) u20 LEFT JOIN
                    (SELECT * FROM user_page_access WHERE user_id=:userId AND permission_id IN (SELECT DISTINCT rp.permission_id FROM user_role ur LEFT JOIN role_permission rp ON rp.role_id=ur.role_id WHERE ur.user_id=:userId)) upa20 ON p20.id = upa20.page_id LEFT JOIN
                    (SELECT * FROM directory_user_access WHERE user_id=:userId AND permission_id IN (SELECT DISTINCT rp.permission_id FROM user_role ur LEFT JOIN role_permission rp ON rp.role_id=ur.role_id WHERE ur.user_id=:userId)) dua20 ON p20.directory_id = dua20.directory_id LEFT JOIN
                    (SELECT * FROM group_users WHERE user_id=:userId) gu20 ON u20.id = gu20.user_id LEFT JOIN
                    (SELECT id, is_active FROM cluster  WHERE is_active) ct20 ON ct20.id = gu20.group_id LEFT JOIN
                    group_permissions gp20 ON gp20.group_id = ct20.id LEFT JOIN
                    group_page_access gpa20 ON (ct20.id = gpa20.group_id AND gpa20.page_id=p20.id AND gp20.permission_id=gpa20.permission_id) LEFT JOIN
                    directory_group_access dga20 ON (ct20.id = dga20.group_id AND dga20.directory_id=p20.directory_id AND gp20.permission_id=dga20.permission_id) LEFT JOIN
                    permission pr20 ON (
                        (pr20.id = upa20.permission_id)
                        OR (pr20.id = dua20.permission_id)
                        OR (pr20.id = gpa20.permission_id)
                        OR (pr20.id = dga20.permission_id)
                    )
                WHERE pr20.name=:permission
                GROUP BY pr20.name) isGranted
                                                                """)
    public Long userHasPagePermission(Long userId, Long pageId, String permission);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS(
                SELECT
                    pr20.name
                FROM
                    (SELECT id FROM users WHERE id=:userId) u20 LEFT JOIN
                    (SELECT * FROM directory_user_access WHERE directory_id=:directoryId AND user_id=:userId AND permission_id IN (SELECT DISTINCT rp.permission_id FROM user_role ur LEFT JOIN role_permission rp ON rp.role_id=ur.role_id WHERE ur.user_id=:userId)) dua20 ON u20.id = dua20.user_id LEFT JOIN
                    (SELECT * FROM group_users WHERE user_id=:userId) gu20 ON u20.id = gu20.user_id LEFT JOIN
                    (SELECT id, is_active FROM cluster  WHERE is_active) ct20 ON ct20.id = gu20.group_id LEFT JOIN
                    group_permissions gp20 ON gp20.group_id = ct20.id LEFT JOIN
                    (SELECT * FROM directory_group_access WHERE directory_id=:directoryId) dga20 ON (ct20.id = dga20.group_id AND gp20.permission_id=dga20.permission_id) LEFT JOIN
                    permission pr20 ON (
                        (pr20.id = dua20.permission_id)
                        OR (pr20.id = dga20.permission_id)
                    )
                WHERE pr20.name=:permission
                GROUP BY pr20.name) isGranted
                                                                    """)
    public Long userHasDirectoryPermission(Long userId, Long directoryId, String permission);

    @Query("SELECT u FROM users u JOIN u.role r WHERE (CONCAT(u.firstName, ' ', u.lastName) LIKE %:searchKey% OR u.email LIKE %:searchKey%) AND u.status = :statusFilter AND r.roleName = :roleFilter")
    Page<User> findAllByFullNameAndStatusAndRole(@Param("searchKey") String searchKey,
            @Param("statusFilter") String statusFilter, @Param("roleFilter") String roleFilter, Pageable pageable);

    @Query("SELECT u FROM users u JOIN u.role r WHERE (CONCAT(u.firstName, ' ', u.lastName) LIKE %:searchKey% OR u.email LIKE %:searchKey%) AND u.status = :statusFilter")
    Page<User> findAllByFullNameAndStatus(@Param("searchKey") String searchKey,
            @Param("statusFilter") String statusFilter, Pageable pageable);

    @Query("SELECT u FROM users u JOIN u.role r WHERE (CONCAT(u.firstName, ' ', u.lastName) LIKE %:searchKey% OR u.email LIKE %:searchKey%) AND r.roleName = :roleFilter")
    Page<User> findAllByFullNameAndRole(@Param("searchKey") String searchKey, @Param("roleFilter") String roleFilter,
            Pageable pageable);

    @Query("SELECT u FROM users u JOIN u.role r WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %:searchKey% OR u.email LIKE %:searchKey%")
    Page<User> findAllByFullName(@Param("searchKey") String searchKey, Pageable pageable);

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
                page_version pv
                    LEFT JOIN
                page p ON pv.page_id = p.id
                    LEFT JOIN
                directory d ON d.id = p.directory_id
                    LEFT JOIN
                workflow w ON d.id = w.directory_id
                    LEFT JOIN
                workflow_step ws ON w.id = ws.workflow_id
                    LEFT JOIN
                workflow_step_approver wsp ON wsp.workflow_step_id = ws.id
                    LEFT JOIN
                users u ON wsp.approver_id = u.id
                    LEFT JOIN
                (SELECT
                    page_version_id, COUNT(*) AS pendingReviewCount
                FROM
                    review r3
                WHERE
                    status = 'PENDING'
                GROUP BY page_version_id) rv3 ON rv3.page_version_id = pv.id
            WHERE
                u.id = :userId AND p.id=:pageId AND pv.id = :versionId
                    AND ws.step = ((SELECT
                        COALESCE(MAX(ws2.step), 0)
                    FROM
                        review r
                            LEFT JOIN
                        workflow_step ws2 ON ws2.id = r.workflow_step_id
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
