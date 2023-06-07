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
            (EXISTS( SELECT
                    1
                FROM
                    (SELECT
                        p.id
                    FROM
                        users u
                    LEFT JOIN group_users ug ON u.id = ug.user_id
                    LEFT JOIN group_rights gr ON ug.group_id = gr.group_id
                    LEFT JOIN page_rights pr ON gr.rights_id = pr.id
                    LEFT JOIN permission p ON pr.permission_id = p.id
                    LEFT JOIN page_rights pr2 ON p.id = pr2.permission_id
                    WHERE
                        p.name=:permission AND u.id=:userId
                            AND pr2.page_id=:pageId) AS userPermission1)
                OR EXISTS( SELECT
                    1
                FROM
                    (SELECT
                        p.id
                    FROM
                        users u
                    LEFT JOIN user_rights ur ON u.id = ur.user_id
                    LEFT JOIN page_rights pr ON ur.rights_id = pr.id
                    LEFT JOIN permission p ON pr.permission_id = p.id
                    LEFT JOIN page_rights pr2 ON p.id = pr2.permission_id
                    WHERE
                        p.name=:permission AND u.id=:userId
                            AND pr2.page_id=:pageId) AS userPermission2)
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
                        p.name=:permission AND u.id=:userId) AS userPermission3)) AS isGranted
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
