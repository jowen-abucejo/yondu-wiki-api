package com.yondu.knowledgebase.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yondu.knowledgebase.entities.PageVersion;

public interface PageVersionRepository extends JpaRepository<PageVersion, Long> {

    @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
    public Optional<PageVersion> findTopByPageIdAndPageDeletedAndReviewsStatusOrderByDateModifiedDesc(
            Long id, boolean isDeleted, String status);

    @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
    public Optional<PageVersion> findTopByPageIdAndPageTypeAndPageDeletedAndReviewsStatusOrderByDateModifiedDesc(
            Long id, String pageType, boolean isDeleted, String status);

    @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
    public Optional<PageVersion> findByPageIdAndId(Long pageId, Long id);

    @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
    public Optional<PageVersion> findByPageIdAndPageTypeAndId(Long pageId, String pageType, Long id);

    @EntityGraph(attributePaths = { "page" })
    public Optional<PageVersion> findTopByPageIdAndPageTypeAndPageDeletedOrderByDateModifiedDesc(
            Long id, String pageType, boolean isDeleted);

    @Query(nativeQuery = true, value = """
            SELECT
                p.date_created AS dateCreated,
                v.date_modified AS dateModified,
                CASE
                    WHEN
                        NOT :isExactMatch OR NOT :searchKey=''
                    THEN
                        ROUND((
                            (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.2) +
                            (MATCH (v.title , v.content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.8)),3)
                    ELSE 1.0
                END AS relevance,
                COALESCE(c.totalComments, 0) AS totalComments,
                COALESCE(r.totalRatings, 0) AS totalRatings,
                COALESCE(rv.totalApprovedReviews, 0) AS totalApprovedReviews,
                COALESCE(rv2.totalDisapprovedReviews, 0) AS totalDisapprovedReviews,
                COALESCE(rv3.totalPendingReviews, 0) AS totalPendingReviews,
                v.id AS versionId,
                v.title AS versionTitle,
                v.original_content AS versionContent,
                a.first_name AS authorFirstName,
                a.last_name AS authorLastName,
                a.email AS authorEmail,
                a.profile_photo AS authorProfilePhoto,
                a.position AS authorPosition,
                mb.first_name AS modifiedByFirstName,
                mb.last_name AS modifiedByLastName,
                mb.email AS modifiedByEmail,
                mb.profile_photo AS modifiedByProfilePhoto,
                mb.position AS modifiedByPosition,
                p.id AS pageId,
                p.is_active AS isActive,
                p.allow_comment AS allowComment,
                p.lock_start AS lockStart,
                p.lock_end AS lockEnd,
                p.page_type AS pageType,
                (SELECT GROUP_CONCAT(t.name SEPARATOR '|') FROM page_tag pt LEFT JOIN tag t ON pt.tag_id = t.id WHERE pt.page_id = p.id LIMIT 1) AS pageTags,
                (SELECT GROUP_CONCAT(ct.name SEPARATOR '|') FROM page_category pcat LEFT JOIN category ct ON pcat.category_id = ct.id WHERE pcat.page_id = p.id LIMIT 1) AS pageCategories
            FROM
                page_version v JOIN
                page p ON v.page_id = p.id LEFT JOIN
                users mb ON v.modified_by = mb.id LEFT JOIN
                users a ON p.author = a.id LEFT JOIN
                (SELECT entity_id, COUNT(*) AS totalComments FROM comment WHERE entity_type = 'PAGE' GROUP BY entity_id) c ON c.entity_id = p.id LEFT JOIN
                (SELECT entity_id, COUNT(*) AS totalRatings FROM rating WHERE entity_type = 'PAGE' AND rating = 'UP' GROUP BY entity_id) r ON r.entity_id = p.id LEFT JOIN
                (SELECT page_version_id, COUNT(*) AS totalApprovedReviews FROM review WHERE status = 'APPROVED' GROUP BY page_version_id ) rv ON rv.page_version_id = v.id LEFT JOIN
                (SELECT page_version_id, COUNT(*) AS totalDisapprovedReviews FROM review WHERE status = 'DISAPPROVED' GROUP BY page_version_id) rv2 ON rv2.page_version_id = v.id LEFT JOIN
                (SELECT page_version_id, COUNT(*) AS totalPendingReviews FROM review WHERE status = 'PENDING' GROUP BY page_version_id) rv3 ON rv3.page_version_id = v.id
            WHERE
                p.page_type = :pageTypeFilter
                AND CASE
                    WHEN :parentDirectory IS NOT NULL
                    THEN
                        p.directory_id = :parentDirectory
                    ELSE TRUE
                    END
                AND CASE
                    WHEN :pagePrimaryKeys IS NOT NULL AND :pagePrimaryKeys <> ''
                    THEN
                        v.page_id IN (:pagePrimaryKeys)
                    ELSE TRUE
                    END
                AND p.is_deleted = 0
                AND (
                        CASE WHEN :isPublished OR :allVersions
                            THEN (
                                (
                                    (:allVersions AND (v.page_id , v.id) IN
                                        (SELECT pv.page_id, pv.id FROM page_version pv WHERE EXISTS
                                            (SELECT 1 FROM review r2 WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id)
                                        GROUP BY pv.page_id)
                                        )
                                    OR (v.page_id , v.id) IN
                                    (SELECT pv.page_id, MAX(pv.id) FROM page_version pv WHERE EXISTS
                                        (SELECT 1 FROM review r2 WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id)
                                    GROUP BY pv.page_id)
                                    )
                                AND (
                                    EXISTS
                                        (SELECT 1 FROM
                                            (SELECT r10.id FROM users u10
                                                LEFT JOIN user_role ur10 ON u10.id = ur10.user_id
                                                LEFT JOIN role r10 ON ur10.role_id = r10.id
                                                WHERE r10.role_name = 'Super Admin' AND u10.id = :userId)
                                        AS pTableX)
                                    OR EXISTS(SELECT 1 FROM
                                        (SELECT p10.id FROM users u10 LEFT JOIN
                                            user_role ur10 ON u10.id = ur10.user_id LEFT JOIN
                                            role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN
                                            permission p10 ON rp10.permission_id = p10.id
                                            WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId)
                                        pTable05)
                                    AND (
                                        EXISTS(SELECT 1 FROM
                                            (SELECT upa10.page_id FROM users u10 LEFT JOIN
                                                user_page_access upa10 ON u10.id = upa10.user_id LEFT JOIN
                                                permission p10 ON upa10.permission_id = p10.id
                                                WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND upa10.page_id = p.id)
                                            pTable00)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT gpa10.page_id FROM users u10 LEFT JOIN
                                                group_users gu10 ON u10.id = gu10.user_id LEFT JOIN
                                                group_page_access gpa10 ON gu10.group_id = gpa10.group_id LEFT JOIN
                                                permission p10 ON gpa10.permission_id = p.id
                                                WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND gpa10.page_id = p.id)
                                            pTable02)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT p10.id FROM users u10 LEFT JOIN
                                                directory_user_access dua10 ON u10.id = dua10.user_id LEFT JOIN
                                                permission p10 ON dua10.permission_id = p10.id
                                                WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND dua10.directory_id = p.directory_id)
                                            pTable03)
                                        )
                                    )
                                )
                            ELSE TRUE
                            END
                        AND
                        CASE WHEN NOT :isPublished OR :allVersions
                            THEN (
                                (v.page_id , v.id) IN
                                    (SELECT pv.page_id, pv.id FROM page_version pv WHERE NOT EXISTS
                                        (SELECT 1 FROM review r2 WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id)
                                    GROUP BY pv.page_id)
                                AND (
                                    EXISTS
                                        (SELECT 1 FROM
                                            (SELECT r10.id FROM users u10
                                                LEFT JOIN user_role ur10 ON u10.id = ur10.user_id
                                                LEFT JOIN role r10 ON ur10.role_id = r10.id
                                                WHERE r10.role_name = 'Super Admin' AND u10.id = :userId)
                                        AS pTableX)
                                    OR EXISTS(SELECT 1 FROM
                                        (SELECT p10.id FROM users u10 LEFT JOIN
                                            user_role ur10 ON u10.id = ur10.user_id LEFT JOIN
                                            role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN
                                            permission p10 ON rp10.permission_id = p10.id
                                            WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                            AND u10.id = :userId)
                                        pTable05)
                                    AND (
                                        p.author = :userId
                                        OR EXISTS(SELECT  1 FROM
                                            (SELECT upa10.page_id FROM users u10 LEFT JOIN
                                            user_page_access upa10 ON u10.id = upa10.user_id LEFT JOIN
                                            permission p10 ON upa10.permission_id = p10.id
                                            WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                            AND u10.id = :userId AND upa10.page_id = p.id)
                                        pTable00)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT gpa10.page_id FROM users u10 LEFT JOIN
                                                group_users gu10 ON u10.id = gu10.user_id LEFT JOIN
                                                group_page_access gpa10 ON gu10.group_id = gpa10.group_id LEFT JOIN
                                                permission p10 ON gpa10.permission_id = p10.id
                                                WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                                AND u10.id = :userId AND gpa10.page_id = p.id)
                                            pTable02)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT p10.id FROM users u10
                                                LEFT JOIN directory_user_access dua10 ON u10.id = dua10.user_id LEFT JOIN
                                                permission p10 ON dua10.permission_id = p10.id
                                                WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                                AND u10.id = :userId AND dua10.directory_id = p.directory_id)
                                            pTable03)
                                        )
                                    )
                                )
                            ELSE TRUE
                            END
                    )
                AND p.is_active <> :isArchived
                AND CASE
                    WHEN :categories IS NOT NULL AND :categories <> ''
                    THEN
                        (v.page_id IN
                            (SELECT pcat2.page_id FROM page_category pcat2
                                LEFT JOIN category cat2 ON pcat2.category_id = cat2.id
                                WHERE cat2.name IN (:categories))
                            )
                    ELSE TRUE
                    END
                AND CASE
                    WHEN :tags IS NOT NULL AND :tags <> ''
                    THEN
                        (v.page_id IN (SELECT ptag.page_id FROM
                            page_tag ptag LEFT JOIN
                            tag tag2 ON ptag.tag_id = tag2.id
                            WHERE tag2.name IN (:tags))
                            )
                    ELSE TRUE
                    END
                AND CASE
                    WHEN :isExactMatch OR :searchKey=''
                    THEN
                        (a.first_name LIKE CONCAT('%', :searchKey, '%')
                            OR a.last_name LIKE CONCAT('%', :searchKey, '%')
                            OR v.title LIKE CONCAT('%', :searchKey, '%')
                            OR v.content LIKE CONCAT('%', :searchKey, '%'))
                    ELSE (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0
                        OR MATCH (v.title , v.content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0)
                    END
                """, countQuery = """
            SELECT COUNT(*)
            FROM
                page_version v JOIN
                page p ON v.page_id = p.id LEFT JOIN
                users mb ON v.modified_by = mb.id LEFT JOIN
                users a ON p.author = a.id LEFT JOIN
                (SELECT entity_id, COUNT(*) AS totalComments FROM comment WHERE entity_type = 'PAGE' GROUP BY entity_id) c ON c.entity_id = p.id LEFT JOIN
                (SELECT entity_id, COUNT(*) AS totalRatings FROM rating WHERE entity_type = 'PAGE' AND rating = 'UP' GROUP BY entity_id) r ON r.entity_id = p.id LEFT JOIN
                (SELECT page_version_id, COUNT(*) AS totalApprovedReviews FROM review WHERE status = 'APPROVED' GROUP BY page_version_id ) rv ON rv.page_version_id = v.id LEFT JOIN
                (SELECT page_version_id, COUNT(*) AS totalDisapprovedReviews FROM review WHERE status = 'DISAPPROVED' GROUP BY page_version_id) rv2 ON rv2.page_version_id = v.id LEFT JOIN
                (SELECT page_version_id, COUNT(*) AS totalPendingReviews FROM review WHERE status = 'PENDING' GROUP BY page_version_id) rv3 ON rv3.page_version_id = v.id
            WHERE
                p.page_type = :pageTypeFilter
                AND CASE
                    WHEN :parentDirectory IS NOT NULL
                    THEN
                        p.directory_id = :parentDirectory
                    ELSE TRUE
                    END
                AND CASE
                    WHEN :pagePrimaryKeys IS NOT NULL AND :pagePrimaryKeys <> ''
                    THEN
                        v.page_id IN (:pagePrimaryKeys)
                    ELSE TRUE
                    END
                AND p.is_deleted = 0
                AND (
                        CASE WHEN :isPublished OR :allVersions
                            THEN (
                                (
                                    (:allVersions AND (v.page_id , v.id) IN
                                        (SELECT pv.page_id, pv.id FROM page_version pv WHERE EXISTS
                                            (SELECT 1 FROM review r2 WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id)
                                        GROUP BY pv.page_id)
                                        )
                                    OR (v.page_id , v.id) IN
                                        (SELECT pv.page_id, MAX(pv.id) FROM page_version pv WHERE EXISTS
                                            (SELECT 1 FROM review r2 WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id)
                                        GROUP BY pv.page_id)
                                    )
                                AND (
                                    EXISTS
                                        (SELECT 1 FROM
                                            (SELECT r10.id FROM users u10
                                                LEFT JOIN user_role ur10 ON u10.id = ur10.user_id
                                                LEFT JOIN role r10 ON ur10.role_id = r10.id
                                                WHERE r10.role_name = 'Super Admin' AND u10.id = :userId)
                                        AS pTableX)
                                    OR EXISTS(SELECT 1 FROM
                                        (SELECT p10.id FROM users u10 LEFT JOIN
                                            user_role ur10 ON u10.id = ur10.user_id LEFT JOIN
                                            role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN
                                            permission p10 ON rp10.permission_id = p10.id
                                            WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId)
                                        pTable05)
                                    AND (
                                        EXISTS(SELECT 1 FROM
                                            (SELECT upa10.page_id FROM users u10 LEFT JOIN
                                                user_page_access upa10 ON u10.id = upa10.user_id LEFT JOIN
                                                permission p10 ON upa10.permission_id = p10.id
                                                WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND upa10.page_id = p.id)
                                            pTable00)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT gpa10.page_id FROM users u10 LEFT JOIN
                                                group_users gu10 ON u10.id = gu10.user_id LEFT JOIN
                                                group_page_access gpa10 ON gu10.group_id = gpa10.group_id LEFT JOIN
                                                permission p10 ON gpa10.permission_id = p.id
                                                WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND gpa10.page_id = p.id)
                                            pTable02)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT p10.id FROM users u10 LEFT JOIN
                                                directory_user_access dua10 ON u10.id = dua10.user_id LEFT JOIN
                                                permission p10 ON dua10.permission_id = p10.id
                                                WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND dua10.directory_id = p.directory_id)
                                            pTable03)
                                        )
                                    )
                                )
                            ELSE TRUE
                            END
                        AND
                        CASE WHEN (NOT :isPublished) OR :allVersions
                            THEN (
                                (v.page_id , v.id) IN
                                    (SELECT pv.page_id, pv.id FROM page_version pv WHERE NOT EXISTS
                                        (SELECT 1 FROM review r2 WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id)
                                    GROUP BY pv.page_id)
                                AND (
                                    EXISTS
                                        (SELECT 1 FROM
                                            (SELECT r10.id FROM users u10
                                                LEFT JOIN user_role ur10 ON u10.id = ur10.user_id
                                                LEFT JOIN role r10 ON ur10.role_id = r10.id
                                                WHERE r10.role_name = 'Super Admin' AND u10.id = :userId)
                                        AS pTableX)
                                    OR EXISTS(SELECT 1 FROM
                                        (SELECT p10.id FROM users u10 LEFT JOIN
                                            user_role ur10 ON u10.id = ur10.user_id LEFT JOIN
                                            role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN
                                            permission p10 ON rp10.permission_id = p10.id
                                            WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                            AND u10.id = :userId)
                                        pTable05)
                                    AND (
                                        p.author = :userId
                                        OR EXISTS(SELECT  1 FROM
                                            (SELECT upa10.page_id FROM users u10 LEFT JOIN
                                            user_page_access upa10 ON u10.id = upa10.user_id LEFT JOIN
                                            permission p10 ON upa10.permission_id = p10.id
                                            WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                            AND u10.id = :userId AND upa10.page_id = p.id)
                                        pTable00)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT gpa10.page_id FROM users u10 LEFT JOIN
                                                group_users gu10 ON u10.id = gu10.user_id LEFT JOIN
                                                group_page_access gpa10 ON gu10.group_id = gpa10.group_id LEFT JOIN
                                                permission p10 ON gpa10.permission_id = p10.id
                                                WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                                AND u10.id = :userId AND gpa10.page_id = p.id)
                                            pTable02)
                                        OR EXISTS(SELECT 1 FROM
                                            (SELECT p10.id FROM users u10
                                                LEFT JOIN directory_user_access dua10 ON u10.id = dua10.user_id LEFT JOIN
                                                permission p10 ON dua10.permission_id = p10.id
                                                WHERE (p10.name = 'CONTENT_APPROVAL' OR p10.name = 'UPDATE_CONTENT')
                                                AND u10.id = :userId AND dua10.directory_id = p.directory_id)
                                            pTable03)
                                        )
                                    )
                                )
                            ELSE TRUE
                            END
                    )
                AND p.is_active <> :isArchived
                AND CASE
                    WHEN :categories IS NOT NULL AND :categories <> ''
                    THEN
                        (v.page_id IN
                            (SELECT pcat2.page_id FROM page_category pcat2
                                LEFT JOIN category cat2 ON pcat2.category_id = cat2.id
                                WHERE cat2.name IN (:categories))
                            )
                    ELSE TRUE
                    END
                AND CASE
                    WHEN :tags IS NOT NULL AND :tags <> ''
                    THEN
                        (v.page_id IN (SELECT ptag.page_id FROM
                            page_tag ptag LEFT JOIN
                            tag tag2 ON ptag.tag_id = tag2.id
                            WHERE tag2.name IN (:tags))
                            )
                    ELSE TRUE
                    END
                AND CASE
                    WHEN :isExactMatch OR :searchKey=''
                    THEN
                        (a.first_name LIKE CONCAT('%', :searchKey, '%')
                            OR a.last_name LIKE CONCAT('%', :searchKey, '%')
                            OR v.title LIKE CONCAT('%', :searchKey, '%')
                            OR v.content LIKE CONCAT('%', :searchKey, '%'))
                    ELSE (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0
                        OR MATCH (v.title , v.content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0)
                    END
            """)
    Optional<Page<Map<String, Object>>> findByFullTextSearch(
            @Param("pageTypeFilter") String pageType,
            @Param("searchKey") String searchKey,
            @Param("isExactMatch") Boolean isExactMatch,
            @Param("isArchived") Boolean isArchived,
            @Param("isPublished") Boolean isPublished,
            @Param("allVersions") Boolean allVersions,
            @Param("categories") List<String> categories,
            @Param("tags") List<String> tags,
            @Param("userId") Long userId,
            @Param("pagePrimaryKeys") List<Long> pageIds,
            @Param("parentDirectory") Long directoryId,
            Pageable pageable);

    // TODO to be refactored
    @EntityGraph(attributePaths = { "page" })
    public Optional<PageVersion> findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(Long id, boolean isDeleted);

}
