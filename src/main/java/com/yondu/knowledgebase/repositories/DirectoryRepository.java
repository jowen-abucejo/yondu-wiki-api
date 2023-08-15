package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Directory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findByNameAndParent(String name, Directory parent);

    @Query(nativeQuery = true, value = """
                SELECT DISTINCT d10.* FROM
                (SELECT * FROM users WHERE id = :userId) u10 LEFT JOIN
                group_users gu10 ON u10.id = gu10.user_id LEFT JOIN
                (SELECT id FROM cluster WHERE is_active) c10 ON c10.id = gu10.group_id LEFT JOIN
                group_permissions gp10 ON c10.id=gp10.group_id  LEFT JOIN
                user_role ur10 ON u10.id = ur10.user_id LEFT JOIN
                role_permission rp10 ON rp10.role_id = ur10.role_id LEFT JOIN
                (SELECT id,name FROM permission WHERE name= :permission) p10 ON (gp10.permission_id=p10.id OR rp10.permission_id=p10.id) LEFT JOIN
                directory_user_access dua10 ON (u10.id=dua10.user_id AND p10.id=dua10.permission_id) LEFT JOIN
                directory_group_access dga10 ON (c10.id=dga10.group_id AND p10.id=dga10.permission_id) LEFT JOIN
                directory d10 ON (d10.id=dua10.directory_id OR d10.id=dga10.directory_id)
                WHERE d10.id IS NOT NULL ORDER BY d10.name
            """, countQuery = """
            SELECT  COUNT(DISTINCT d10.id) FROM
            (SELECT * FROM users WHERE id = :userId) u10 LEFT JOIN
            group_users gu10 ON u10.id = gu10.user_id LEFT JOIN
            (SELECT id FROM cluster WHERE is_active) c10 ON c10.id = gu10.group_id LEFT JOIN
            group_permissions gp10 ON c10.id=gp10.group_id  LEFT JOIN
            user_role ur10 ON u10.id = ur10.user_id LEFT JOIN
            role_permission rp10 ON rp10.role_id = ur10.role_id LEFT JOIN
            (SELECT id,name FROM permission WHERE name= :permission) p10 ON (gp10.permission_id=p10.id OR rp10.permission_id=p10.id) LEFT JOIN
            directory_user_access dua10 ON (u10.id=dua10.user_id AND p10.id=dua10.permission_id) LEFT JOIN
            directory_group_access dga10 ON (c10.id=dga10.group_id AND p10.id=dga10.permission_id) LEFT JOIN
            directory d10 ON (d10.id=dua10.directory_id OR d10.id=dga10.directory_id)
            WHERE d10.id IS NOT NULL
            """)
    Optional<Page<Directory>> findByDirectoryUserAccessesPermissionNameAndDirectoryUserAccessesUserId(String permission,
            Long userId, Pageable paging);
}
