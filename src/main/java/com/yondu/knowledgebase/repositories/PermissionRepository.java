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
                DISTINCT pr20.id
            FROM
                (SELECT
                    *
                FROM
                    users
                WHERE
                    id = :userId) u20
                    LEFT JOIN
                user_page_access upa20 ON u20.id = upa20.user_id
                    LEFT JOIN
                directory_user_access dua20 ON u20.id = dua20.user_id
                    LEFT JOIN
                group_users gu20 ON u20.id = gu20.user_id
                    LEFT JOIN
                (SELECT
                    id, is_active
                FROM
                    cluster
                WHERE
                    is_active) ct20 ON ct20.id = gu20.group_id
                    LEFT JOIN
                group_page_access gpa20 ON ct20.id = gpa20.group_id
                    LEFT JOIN
                directory_group_access dga20 ON ct20.id = dga20.group_id
                    LEFT JOIN
                page p20 ON (p20.id = upa20.page_id
                    OR p20.directory_id = dga20.directory_id
                    OR p20.directory_id = dua20.directory_id
                    OR p20.id = gpa20.page_id)
                    LEFT JOIN
                permission pr20 ON ((pr20.id = upa20.permission_id
                    AND upa20.page_id = p20.id)
                    OR (pr20.id = dua20.permission_id
                    AND dua20.directory_id = p20.directory_id)
                    OR (pr20.id = gpa20.permission_id
                    AND gpa20.page_id = p20.id)
                    OR (pr20.id = dga20.permission_id
                    AND dga20.directory_id = p20.directory_id))
            WHERE p20.id=:pageId
                    """)
    public Set<Long> findAllDistinctIdByPageIdAndUserId(Long pageId, Long userId);

    @Query(nativeQuery = true, value = """
            SELECT DISTINCT
                pr.id
            FROM
                users u
                    LEFT JOIN
                directory_user_access dua ON u.id = dua.user_id
                    LEFT JOIN
                group_users gu ON u.id = gu.user_id
                    LEFT JOIN
                (SELECT
                    id, is_active
                FROM
                    cluster
                WHERE
                    is_active) ct ON ct.id = gu.group_id
                    LEFT JOIN
                directory_group_access dga ON ct.id = dga.group_id
                    LEFT JOIN
                permission pr ON (
                    pr.id = dua.permission_id
                    OR pr.id = dga.permission_id)
            WHERE
                u.id = :userId AND (dua.directory_id = :directoryId OR dga.directory_id = :directoryId)
                """)
    public Set<Long> findAllDistinctIdByDirectoryIdAndUserId(Long directoryId, Long userId);
}
