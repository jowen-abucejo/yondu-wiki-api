package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findAllByCategory(String category);

    List<Permission> findAllByCategoryOrCategoryOrCategoryOrCategory(String category1, String category2,
            String category3, String category4);

    @Query(nativeQuery = true, value = """
            SELECT
                pr20.id
            FROM
                (
                    SELECT p.id, p.directory_id
                    FROM page p
                    WHERE
                    p.id = :pageId
                ) p20 LEFT JOIN
                (SELECT * FROM user_page_access WHERE user_id=:userId AND permission_id IN (SELECT DISTINCT rp.permission_id FROM user_role ur LEFT JOIN role_permission rp ON rp.role_id=ur.role_id WHERE ur.user_id=:userId)) upa20 ON p20.id = upa20.page_id LEFT JOIN
                (SELECT * FROM directory_user_access WHERE user_id=:userId AND permission_id IN (SELECT DISTINCT rp.permission_id FROM user_role ur LEFT JOIN role_permission rp ON rp.role_id=ur.role_id WHERE ur.user_id=:userId)) dua20 ON p20.directory_id = dua20.directory_id LEFT JOIN
                (SELECT * FROM group_users WHERE user_id=:userId) gu20 ON dua20.user_id = gu20.user_id LEFT JOIN
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
            GROUP BY pr20.id
                        """)
    public Set<Long> findAllDistinctIdByPageIdAndUserId(Long pageId, Long userId);

    @Query(nativeQuery = true, value = """
            SELECT
                pr20.id
            FROM
                (SELECT * FROM directory_user_access WHERE user_id=:userId AND directory_id = :directoryId AND permission_id IN (SELECT DISTINCT rp.permission_id FROM user_role ur LEFT JOIN role_permission rp ON rp.role_id=ur.role_id WHERE ur.user_id=:userId)) dua20 LEFT JOIN
                (SELECT * FROM group_users WHERE user_id=:userId) gu20 ON dua20.user_id = gu20.user_id LEFT JOIN
                (SELECT id, is_active FROM cluster WHERE is_active) ct20 ON ct20.id = gu20.group_id LEFT JOIN
                group_permissions gp20 ON gp20.group_id = ct20.id LEFT JOIN
                directory_group_access dga20 ON (ct20.id = dga20.group_id AND dga20.directory_id=dua20.directory_id AND gp20.permission_id=dga20.permission_id) LEFT JOIN
                permission pr20 ON (
                    (pr20.id = dua20.permission_id)
                    OR (pr20.id = dga20.permission_id)
                )
            GROUP BY pr20.id
                    """)
    public Set<Long> findAllDistinctIdByDirectoryIdAndUserId(Long directoryId, Long userId);
}
