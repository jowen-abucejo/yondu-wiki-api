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
            SELECT DISTINCT
                pr.id
            FROM
                users u
                    LEFT JOIN
                user_page_access upa ON u.id = upa.user_id
                    LEFT JOIN
                directory_user_access dua ON u.id = dua.user_id
                    LEFT JOIN
                directory d ON dua.directory_id = d.id
                    LEFT JOIN
                group_users gu ON u.id = gu.user_id
                    LEFT JOIN
                group_page_access gpa ON gu.group_id = gpa.group_id
                    LEFT JOIN
                directory_group_access dga ON gu.group_id = dga.group_id
                    LEFT JOIN
                directory d2 ON dga.directory_id = d2.id
                    LEFT JOIN
                permission pr ON (pr.id = upa.permission_id
                    OR pr.id = dua.permission_id
                    OR pr.id = gpa.permission_id
                    OR pr.id = dga.permission_id)
                    LEFT JOIN
                page p ON (p.id = upa.page_id
                    OR p.directory_id = d.id
                    OR p.directory_id = d2.id
                    OR p.id = gpa.page_id)
            WHERE
                u.id = :userId AND p.id = :pageId
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
                directory_group_access dga ON gu.group_id = dga.group_id
                    LEFT JOIN
                permission pr ON (
                    pr.id = dua.permission_id
                    OR pr.id = dga.permission_id)
            WHERE
                u.id = :userId AND (dua.directory_id = :directoryId OR dga.directory_id = :directoryId)
                """)
    public Set<Long> findAllDistinctIdByDirectoryIdAndUserId(Long directoryId, Long userId);
}
