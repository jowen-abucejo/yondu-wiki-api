package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.User;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT u FROM users u WHERE u.email LIKE ?1 OR u.username LIKE ?1")
    Page<User> findAll(String searchKey, Pageable pageable);

    Optional<User> findByEmail(String email);

    User findByFirstNameOrLastName(String firstName, String lastName);

    @Query(nativeQuery = true, value = """
            SELECT
            CASE
                WHEN(
                        EXISTS(
                            SELECT
                                1
                            FROM
                                (SELECT
                                    p10.id
                                FROM
                                    users u10
                                LEFT JOIN user_role ur10 ON u10.id = ur10.user_id
                                LEFT JOIN role_permission rp10 ON ur10.role_id = rp10.role_id
                                LEFT JOIN permission p10 ON rp10.permission_id = p10.id
                                WHERE
                                    p10.name = :permission
                                        AND u10.id = :userId) pTable05
                        )
                        OR EXISTS(
                            SELECT
                                1
                            FROM
                                (SELECT
                                    pr10.page_id
                                FROM
                                    users u10
                                LEFT JOIN user_rights ur10 ON u10.id = ur10.user_id
                                LEFT JOIN page_rights pr10 ON ur10.rights_id = pr10.id
                                LEFT JOIN permission p10 ON pr10.permission_id = p10.id
                                WHERE
                                    p10.name = :permission
                                        AND u10.id = :userId
                                        AND pr10.page_id = :pageId) pTable00
                        )
                        OR EXISTS(
                            SELECT
                                1
                            FROM
                                (SELECT
                                    pr10.page_id
                                FROM
                                    users u10
                                LEFT JOIN group_users gu10 ON u10.id = gu10.user_id
                                LEFT JOIN group_rights gr10 ON gu10.group_id = gr10.group_id
                                LEFT JOIN page_rights pr10 ON gr10.rights_id = pr10.id
                                LEFT JOIN permission p10 ON pr10.permission_id = p10.id
                                WHERE
                                    p10.name = :permission
                                        AND u10.id = :userId
                                        AND pr10.page_id = :pageId) pTable02
                        )
                    )
                THEN
                    TRUE
                ELSE FALSE
            END AS isGranted
                            """)
    public Boolean userHasPagePermission(Long userId, Long pageId, String permission);

    @Query(nativeQuery = true, value = """
        SELECT
        CASE
            WHEN(
                    EXISTS(
                        SELECT
                            1
                        FROM
                            (SELECT
                                dr10.directory_id
                            FROM
                                users u10
                            LEFT JOIN user_rights ur10 ON u10.id = ur10.user_id
                            LEFT JOIN directory_rights dr10 ON ur10.rights_id = dr10.id
                            LEFT JOIN permission p10 ON dr10.permission_id = p10.id
                            WHERE
                                p10.name = :permission
                                    AND u10.id = :userId
                                    AND dr10.directory_id = directoryId) pTable01
                    )
                    OR EXISTS(
                        SELECT
                            1
                        FROM
                            (SELECT
                                dr10.directory_id
                            FROM
                                users u10
                            LEFT JOIN group_users gu10 ON u10.id = gu10.user_id
                            LEFT JOIN group_rights gr10 ON gu10.group_id = gr10.group_id
                            LEFT JOIN directory_rights dr10 ON gr10.rights_id = dr10.id
                            LEFT JOIN permission p10 ON dr10.permission_id = p10.id
                            WHERE
                                p10.name = :permission
                                    AND u10.id = :userId
                                    AND dr10.directory_id = :directoryId) pTable03
                    )
                )
            THEN
                TRUE
            ELSE FALSE
        END AS isGranted
                                """)
    public Boolean userHasDirectoryPermission(Long userId, Long directoryId, String permission);
}
