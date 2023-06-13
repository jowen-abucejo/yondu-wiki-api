package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Rights;
import com.yondu.knowledgebase.entities.User;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM users u WHERE email = ?1 AND status = 'ACT'")
    Optional<User> fetchUserByEmail(String email);

    @Query("SELECT u FROM users u WHERE email = ?1 AND status = 'ACT'")
    User getUserByEmail(String email);

    @Query("UPDATE users SET status = 'INA' WHERE email = ?1 AND status = 'ACT'")
    void deactivateUserByEmail(String email);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM users u WHERE u.email LIKE ?1 OR u.username LIKE ?1 OR u.firstName LIKE ?1 OR u.lastName LIKE ?1")
    Page<User> findAll(String searchKey, Pageable pageable);

    Optional<User> findByEmail(String email);

    User findByFirstNameOrLastName(String firstName, String lastName);

    @Query("SELECT u.rights FROM users u WHERE u.id = :userId ")
    Set<Rights> findRightsById(Long userId);

    @Query("SELECT u FROM users u JOIN u.role r JOIN r.permissions p WHERE p.name = 'CONTENT_APPROVAL'")
    Set<User> findUsersWithContentApprovalPermission();

    @Query(nativeQuery = true, value = """
            SELECT
                (EXISTS( SELECT
                        1
                    FROM
                        (SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN user_role ur ON u.id = ur.user_id
                        LEFT JOIN role_permission rp ON rp.role_id = ur.role_id
                        LEFT JOIN permission p ON rp.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId) AS userPermission3)
                    AND (EXISTS( SELECT
                        1
                    FROM
                        (SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN user_page_access upa ON u.id = upa.user_id
                        LEFT JOIN permission p ON upa.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId
                                AND upa.page_id = :pageId) AS userPermission)
                    OR EXISTS( SELECT
                        1
                    FROM
                        (SELECT
                            p.id
                        FROM
                            users u
                        LEFT JOIN group_users gu ON u.id = gu.user_id
                        LEFT JOIN group_page_access gpa ON gu.group_id = gpa.group_id
                        LEFT JOIN permission p ON gpa.permission_id = p.id
                        WHERE
                            p.name = :permission AND u.id = :userId
                                AND gpa.page_id = :pageId) AS userPermission2))) as isGranted
                                            """)
    public Long userHasPagePermission(Long userId, Long pageId, String permission);

    @Query(nativeQuery = true, value = """
            SELECT
            (EXISTS( SELECT
                    1
                FROM
                    (SELECT
                        p.id
                    FROM
                        users u
                    LEFT JOIN group_users ug ON u.id = ug.user_id
                    LEFT JOIN group_rights gr ON ug.group_id = gr.group_id
                    LEFT JOIN directory_rights dr ON gr.rights_id = dr.id
                    LEFT JOIN permission p ON dr.permission_id = p.id
                    WHERE
                        p.name = :permission AND u.id = :userId
                            AND dr.directory_id = :directoryId) AS userPermission)
                OR EXISTS( SELECT
                    1
                FROM
                    (SELECT
                        p.id
                    FROM
                        users u
                    LEFT JOIN user_rights ur ON u.id = ur.user_id
                    LEFT JOIN directory_rights dr ON ur.rights_id = dr.id
                    LEFT JOIN permission p ON dr.permission_id = p.id
                    WHERE
                        p.name = :permission AND u.id = :userId
                            AND dr.directory_id = :directoryId) AS userPermission2)
                OR EXISTS( SELECT
                    1
                FROM
                    (SELECT
                        p.id
                    FROM
                        users u
                    LEFT JOIN user_role ur ON u.id = ur.user_id
                    LEFT JOIN role_permission rp ON rp.role_id = ur.role_id
                    LEFT JOIN permission p ON rp.permission_id = p.id
                    WHERE
                        p.name = :permission AND u.id = :userId) AS userPermission3)) AS isGranted
                                        """)
    public Long userHasDirectoryPermission(Long userId, Long directoryId, String permission);
}
