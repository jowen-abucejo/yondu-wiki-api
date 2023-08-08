package com.yondu.knowledgebase.repositories;

import java.time.LocalDateTime;
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
    public Optional<PageVersion> findByPageIdAndId(Long pageId, Long id);

    @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
    public Optional<PageVersion> findByPageIdAndPageTypeAndId(Long pageId, String pageType, Long id);

    @EntityGraph(attributePaths = { "page" })
    public Optional<PageVersion> findTopByPageIdAndPageTypeAndPageDeletedOrderByDateModifiedDesc(
            Long id, String pageType, boolean isDeleted);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*) FROM page_version pv LEFT JOIN
            (SELECT page_version_id,COUNT(*) AS reviewCount FROM reviews GROUP BY page_version_id) a ON pv.id=a.page_version_id
            WHERE a.reviewCount > 0 AND pv.original_content REGEXP CONCAT('<img[^>]*src=\"', :imageUrl, '[^\"]*')
            """)
    Long countByContentWithImageSrc(String imageUrl);

    @Query(nativeQuery = true, value = """
            (
                (SELECT
                    p.date_created AS dateCreated,
                    v.date_modified AS dateModified,
                    CASE
                    WHEN
                        NOT :isExactMatch OR NOT :searchKey=''
                    THEN
                        ROUND((
                            (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.2) +
                            (MATCH (mb.first_name , mb.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.2) +
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
                    lb.first_name AS lockedByFirstName,
                    lb.last_name AS lockedByLastName,
                    lb.email AS lockedByEmail,
                    lb.profile_photo AS lockedByProfilePhoto,
                    lb.position AS lockedByPosition,
                    p.id AS pageId,
                    p.is_active AS isActive,
                    p.allow_comment AS allowComment,
                    p.lock_start AS lockStart,
                    p.lock_end AS lockEnd,
                    p.page_type AS pageType,
                    dr.id as directoryId,
                    dr.name as directoryName,
                    w3.workflow_id as workflowId,
                    w3.totalRequiredApproval as workflowStepCount,
                    allTags.pTags AS pageTags,
                    allCats.pCats AS pageCategories,
                    COALESCE(sp.entity_id, 0) AS isSaved,
                    rrp.rating AS myRating,
                    upp.userPermissions AS userPagePermissions,
                    sp.date_created AS dateSaved,
                    COALESCE(rd.totalDownRatings, 0) AS totalDownRatings,
                    COALESCE(cpt.totalParentComments, 0) AS totalParentComments
                 FROM
                    page_version v JOIN
                    page p ON v.page_id = p.id LEFT JOIN
                    users mb ON v.modified_by = mb.id LEFT JOIN
                    users a ON p.author = a.id LEFT JOIN
                    users lb ON p.locked_by = lb.id LEFT JOIN
                    directory dr ON dr.id = p.directory_id LEFT JOIN
                    workflow w ON w.directory_id=dr.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalComments FROM comment WHERE entity_type = 'PAGE' AND is_deleted = 0 GROUP BY entity_id) c ON c.entity_id = p.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalParentComments FROM comment WHERE entity_type = 'PAGE' AND is_deleted = 0 AND parent_comment_id IS NULL GROUP BY entity_id) cpt ON c.entity_id = p.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalRatings FROM rating WHERE entity_type = 'PAGE' AND rating = 'UP' AND is_active GROUP BY entity_id) r ON r.entity_id = p.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalDownRatings FROM rating WHERE entity_type = 'PAGE' AND rating = 'DOWN' AND is_active GROUP BY entity_id) rd ON rd.entity_id = p.id LEFT JOIN
                    (SELECT r2.page_version_id, ws2.workflow_id, COUNT(*) AS totalApprovedReviews FROM review r2 LEFT JOIN workflow_step ws2 ON ws2.id=r2.workflow_step_id WHERE r2.status = 'APPROVED' GROUP BY r2.page_version_id, ws2.workflow_id) rv ON rv.page_version_id = v.id AND rv.workflow_id=w.id LEFT JOIN
                    (SELECT r3.page_version_id, ws3.workflow_id, COUNT(*) AS totalDisapprovedReviews FROM review r3 LEFT JOIN workflow_step ws3 ON ws3.id=r3.workflow_step_id WHERE r3.status = 'DISAPPROVED' GROUP BY r3.page_version_id, ws3.workflow_id) rv2 ON rv2.page_version_id = v.id AND rv2.workflow_id=w.id LEFT JOIN
                    (SELECT page_version_id, COUNT(*) AS totalPendingReviews FROM review WHERE status = 'PENDING' GROUP BY page_version_id) rv3 ON rv3.page_version_id = v.id LEFT JOIN
                    (SELECT workflow_id, COUNT(*) AS totalRequiredApproval FROM workflow_step GROUP BY workflow_id) w3 ON w3.workflow_id = w.id LEFT JOIN
                    (SELECT pt.page_id, GROUP_CONCAT(t.name SEPARATOR '|') as pTags FROM page_tag pt LEFT JOIN tag t ON pt.tag_id = t.id GROUP BY pt.page_id) allTags ON allTags.page_id=v.page_id LEFT JOIN
                    (SELECT pcat.page_id, GROUP_CONCAT(ct.name SEPARATOR '|') as pCats FROM page_category pcat LEFT JOIN category ct ON pcat.category_id = ct.id GROUP BY pcat.page_id) allCats ON allCats.page_id=v.page_id LEFT JOIN
                    (SELECT entity_id,author,date_created FROM save WHERE entity_type='PAGE' AND author=:userId GROUP BY entity_id,author,date_created) sp ON sp.entity_id=v.page_id LEFT JOIN
                    (SELECT entity_id,user_id,rating FROM rating WHERE entity_type = 'PAGE' AND user_id = :userId AND is_active GROUP BY entity_id,user_id,rating) rrp ON rrp.entity_id=v.page_id LEFT JOIN
                    (SELECT
                        p20.id AS pageId,
                        GROUP_CONCAT(DISTINCT pr20.id) AS userPermissions,
                        GROUP_CONCAT(DISTINCT pr20.name) AS userPermissionNames
                    FROM
                        (SELECT *FROM users WHERE id = :userId) u20 LEFT JOIN
                        user_page_access upa20 ON u20.id = upa20.user_id LEFT JOIN
                        directory_user_access dua20 ON u20.id = dua20.user_id LEFT JOIN
                        group_users gu20 ON u20.id = gu20.user_id LEFT JOIN
                        (SELECT id, is_active FROM cluster  WHERE is_active) ct20 ON ct20.id = gu20.group_id LEFT JOIN
                        group_page_access gpa20 ON ct20.id = gpa20.group_id LEFT JOIN
                        directory_group_access dga20 ON ct20.id = dga20.group_id LEFT JOIN
                        page p20 ON (
                            p20.id = upa20.page_id
                            OR p20.directory_id = dga20.directory_id
                            OR p20.directory_id = dua20.directory_id
                            OR p20.id = gpa20.page_id
                        ) LEFT JOIN
                        permission pr20 ON (
                            (pr20.id = upa20.permission_id AND upa20.page_id = p20.id)
                            OR (pr20.id = dua20.permission_id AND dua20.directory_id = p20.directory_id)
                            OR (pr20.id = gpa20.permission_id AND gpa20.page_id = p20.id)
                            OR (pr20.id = dga20.permission_id AND dga20.directory_id = p20.directory_id)
                        )
                    GROUP BY p20.id) upp ON upp.pageId=v.page_id
                WHERE
                    FIND_IN_SET(p.page_type, :pageTypeFilter)>0
                    AND p.is_deleted = 0
                    AND CASE WHEN :isArchived IS NOT NULL THEN p.is_active <> :isArchived ELSE TRUE END
                    AND CASE WHEN :author IS NOT NULL AND :author <> '' THEN a.email = :author ELSE TRUE END
                    AND CASE WHEN :savedOnly THEN sp.entity_id = p.id ELSE TRUE END
                    AND CASE WHEN :upVotedOnly THEN rrp.rating = 'UP' ELSE TRUE END
                    AND CASE WHEN :pagePrimaryKeys IS NOT NULL AND :pagePrimaryKeys <> '' THEN FIND_IN_SET(v.page_id, :pagePrimaryKeys)>0 ELSE TRUE END
                    AND CASE WHEN :parentDirectory IS NOT NULL THEN p.directory_id = :parentDirectory ELSE TRUE END
                    AND CASE WHEN :fromDate IS NOT NULL AND :fromDate <> '' THEN DATE(p.date_created) >= :fromDate ELSE TRUE END
                    AND CASE WHEN :categories IS NOT NULL AND :categories <> ''
                        THEN (v.page_id IN (SELECT pcat2.page_id FROM page_category pcat2 LEFT JOIN category cat2 ON pcat2.category_id = cat2.id WHERE FIND_IN_SET(cat2.name, :categories)>0))
                        ELSE TRUE
                        END
                    AND CASE
                        WHEN :tags IS NOT NULL AND :tags <> ''
                        THEN (v.page_id IN (SELECT ptag.page_id FROM page_tag ptag LEFT JOIN tag tag2 ON ptag.tag_id = tag2.id WHERE FIND_IN_SET(tag2.name, :tags)>0) )
                        ELSE TRUE
                        END
                    AND (
                        CASE
                        WHEN :isExactMatch OR :searchKey=''
                        THEN (a.first_name LIKE CONCAT('%', :searchKey, '%') OR a.last_name LIKE CONCAT('%', :searchKey, '%')  OR v.title LIKE CONCAT('%', :searchKey, '%') OR v.content LIKE CONCAT('%', :searchKey, '%'))
                        ELSE (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0 OR MATCH (mb.first_name , mb.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0 OR MATCH (v.title , v.content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0)
                        END
                    )
                    AND (
                        CASE
                        WHEN :isPublished OR :allVersions
                        THEN (
                            ((v.page_id , v.id) IN
                                (SELECT pv.page_id, CASE WHEN :allVersions THEN MAX(pv.id) ELSE pv.id END
                                    FROM page_version pv
                                    WHERE EXISTS(SELECT 1 FROM
                                        (SELECT COUNT(*) AS totalApprovedReviews FROM review r2 LEFT JOIN workflow_step ws2 ON ws2.id=r2.workflow_step_id WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id AND ws2.workflow_id = w.id) rCheck
                                            WHERE rCheck.totalApprovedReviews = (SELECT MAX(step) FROM workflow_step WHERE workflow_id=w.id)
                                    )
                                    GROUP BY pv.page_id, pv.id
                                )
                            )
                            AND (
                                EXISTS(SELECT r10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role r10 ON ur10.role_id = r10.id WHERE r10.role_name = 'Administrator' AND u10.id = :userId)
                                OR (
                                    (
                                        EXISTS(SELECT p10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN permission p10 ON rp10.permission_id = p10.id WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId)
                                        OR EXISTS(SELECT gp10.group_id FROM users u10 LEFT JOIN group_users gu10 ON u10.id = gu10.user_id LEFT JOIN cluster c10 ON c10.id = gu10.group_id LEFT JOIN group_permissions gp10 ON gu10.group_id = gp10.group_id LEFT JOIN permission p10 ON gp10.permission_id = p10.id WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND c10.is_active)
                                    )
                                    AND FIND_IN_SET('READ_CONTENT', upp.userPermissionNames) > 0
                                )
                            )
                        )
                        ELSE :isPublished
                        END
                        OR
                        CASE
                        WHEN NOT :isPublished OR :allVersions
                        THEN (
                            (v.page_id , v.id) IN
                            (
                                SELECT pv.page_id, pv.id
                                FROM page_version pv
                                WHERE NOT EXISTS(
                                    SELECT 1
                                    FROM(SELECT COUNT(*) AS totalApprovedReviews FROM review r2 LEFT JOIN workflow_step ws2 ON ws2.id=r2.workflow_step_id WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id AND ws2.workflow_id = w.id) rCheck
                                    WHERE rCheck.totalApprovedReviews = (SELECT MAX(step) FROM workflow_step WHERE workflow_id=w.id)
                                )
                                GROUP BY pv.page_id
                            )
                            AND (
                                (
                                    :pendingOnly
                                    AND
                                    rv3.totalPendingReviews > 0
                                    AND (
                                        EXISTS(SELECT r10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role r10 ON ur10.role_id = r10.id WHERE r10.role_name = 'Administrator' AND u10.id = :userId)
                                        OR EXISTS(SELECT p10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN permission p10 ON rp10.permission_id = p10.id WHERE p10.name = 'CONTENT_APPROVAL' AND u10.id = :userId)
                                        OR EXISTS(SELECT gp10.group_id FROM users u10 LEFT JOIN group_users gu10 ON u10.id = gu10.user_id LEFT JOIN cluster c10 ON c10.id = gu10.group_id LEFT JOIN group_permissions gp10 ON gu10.group_id = gp10.group_id LEFT JOIN permission p10 ON gp10.permission_id = p10.id WHERE p10.name = 'CONTENT_APPROVAL' AND u10.id = :userId AND c10.is_active)
                                    )
                                    AND EXISTS(SELECT u10.id FROM users u10 LEFT JOIN workflow_step_approver wsa10 ON u10.id = wsa10.approver_id LEFT JOIN workflow_step ws10 ON wsa10.workflow_step_id=ws10.id LEFT JOIN workflow w10 ON ws10.workflow_id = w10.id WHERE u10.id = :userId AND w10.directory_id = p.directory_id)
                                )
                                OR (
                                    :draftOnly
                                    AND (
                                        EXISTS(SELECT r10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role r10 ON ur10.role_id = r10.id WHERE r10.role_name = 'Administrator' AND u10.id = :userId)
                                        OR EXISTS(SELECT p10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN permission p10 ON rp10.permission_id = p10.id WHERE p10.name = 'UPDATE_CONTENT' AND u10.id = :userId)
                                        OR EXISTS(SELECT gp10.group_id FROM users u10 LEFT JOIN group_users gu10 ON u10.id = gu10.user_id LEFT JOIN cluster c10 ON c10.id = gu10.group_id LEFT JOIN group_permissions gp10 ON gu10.group_id = gp10.group_id LEFT JOIN permission p10 ON gp10.permission_id = p10.id WHERE p10.name = 'UPDATE_CONTENT' AND u10.id = :userId AND c10.is_active)
                                    )
                                    AND FIND_IN_SET('UPDATE_CONTENT', upp.userPermissionNames) > 0
                                )
                            )
                        )
                        ELSE NOT :isPublished
                        END
                    )
                )
                UNION ALL
                (SELECT
                    p.date_created AS dateCreated,
                    p.date_modified AS dateModified,
                    CASE
                    WHEN
                        NOT :isExactMatch OR NOT :searchKey=''
                    THEN
                        ROUND((
                            (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.4) +
                            (MATCH (p.title , p.modified_content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.8)),3)
                    ELSE 1.0
                    END AS relevance,
                    COALESCE(c.totalComments, 0) AS totalComments,
                    COALESCE(r.totalRatings, 0) AS totalRatings,
                    0 AS totalApprovedReviews,
                    0 AS totalDisapprovedReviews,
                    0 AS totalPendingReviews,
                    p.id AS versionId,
                    p.title AS versionTitle,
                    p.content AS versionContent,
                    a.first_name AS authorFirstName,
                    a.last_name AS authorLastName,
                    a.email AS authorEmail,
                    a.profile_photo AS authorProfilePhoto,
                    a.position AS authorPosition,
                    a.first_name AS modifiedByFirstName,
                    a.last_name AS modifiedByLastName,
                    a.email AS modifiedByEmail,
                    a.profile_photo AS modifiedByProfilePhoto,
                    a.position AS modifiedByPosition,
                    '' AS lockedByFirstName,
                    '' AS lockedByLastName,
                    '' AS lockedByEmail,
                    '' AS lockedByProfilePhoto,
                    '' AS lockedByPosition,
                    p.id AS pageId,
                    p.is_active AS isActive,
                    p.allow_comment AS allowComment,
                    CURRENT_TIMESTAMP AS lockStart,
                    CURRENT_TIMESTAMP AS lockEnd,
                    'DISCUSSION' AS pageType,
                    0 as directoryId,
                    '' as directoryName,
                    0 as workflowId,
                    0 as workflowStepCount,
                    allTags.pTags AS pageTags,
                    allCats.pCats AS pageCategories,
                    COALESCE(sp.entity_id, 0) AS isSaved,
                    rrp.rating AS myRating,
                    '' AS userPagePermissions,
                    sp.date_created AS dateSaved,
                    COALESCE(rd.totalDownRatings, 0) AS totalDownRatings,
                    COALESCE(cpt.totalParentComments, 0) AS totalParentComments
                FROM
                    post p LEFT JOIN
                    users a ON p.author = a.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalComments FROM comment WHERE entity_type = 'POST' AND is_deleted = 0 GROUP BY entity_id) c ON c.entity_id = p.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalParentComments FROM comment WHERE entity_type = 'POST' AND is_deleted = 0 AND parent_comment_id IS NULL GROUP BY entity_id) cpt ON c.entity_id = p.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalRatings FROM rating WHERE entity_type = 'POST' AND rating = 'UP' AND is_active GROUP BY entity_id) r ON r.entity_id = p.id LEFT JOIN
                    (SELECT entity_id, COUNT(*) AS totalDownRatings FROM rating WHERE entity_type = 'POST' AND rating = 'DOWN' AND is_active GROUP BY entity_id) rd ON rd.entity_id = p.id LEFT JOIN
                    (SELECT pt.post_id, GROUP_CONCAT(t.name SEPARATOR '|') as pTags FROM post_tag pt LEFT JOIN tag t ON pt.tag_id = t.id GROUP BY pt.post_id) allTags ON allTags.post_id=p.id LEFT JOIN
                    (SELECT pcat.post_id, GROUP_CONCAT(ct.name SEPARATOR '|') as pCats FROM post_category pcat LEFT JOIN category ct ON pcat.category_id = ct.id GROUP BY pcat.post_id) allCats ON allCats.post_id=p.id LEFT JOIN
                    (SELECT entity_id,author,date_created FROM save WHERE entity_type='POST' AND author=:userId GROUP BY entity_id,author,date_created) sp ON sp.entity_id=p.id LEFT JOIN
                    (SELECT entity_id,user_id,rating FROM rating WHERE entity_type = 'POST' AND user_id = :userId AND is_active GROUP BY entity_id,user_id,rating) rrp ON rrp.entity_id=p.id
                WHERE
                    (FIND_IN_SET('DISCUSSION', :pageTypeFilter)>0 OR FIND_IN_SET('POST', :pageTypeFilter)>0)
                    AND p.is_deleted = 0
                    AND CASE WHEN :isArchived IS NOT NULL THEN p.is_active <> :isArchived ELSE TRUE END
                    AND CASE WHEN :author IS NOT NULL AND :author <> '' THEN a.email = :author ELSE TRUE END
                    AND CASE WHEN :savedOnly THEN sp.entity_id = p.id ELSE TRUE END
                    AND CASE WHEN :upVotedOnly THEN rrp.rating = 'UP' ELSE TRUE END
                    AND CASE WHEN :pagePrimaryKeys IS NOT NULL AND :pagePrimaryKeys <> '' THEN FIND_IN_SET(p.id, :pagePrimaryKeys)>0 ELSE TRUE END
                    AND CASE WHEN :fromDate IS NOT NULL AND :fromDate <> '' THEN DATE(p.date_created) >= :fromDate ELSE TRUE END
                    AND CASE WHEN :categories IS NOT NULL AND :categories <> ''
                        THEN (p.id IN (SELECT pcat2.post_id FROM post_category pcat2 LEFT JOIN category cat2 ON pcat2.category_id = cat2.id WHERE FIND_IN_SET(cat2.name, :categories)>0))
                        ELSE TRUE
                        END
                    AND CASE
                        WHEN :tags IS NOT NULL AND :tags <> ''
                        THEN (p.id IN (SELECT ptag.post_id FROM post_tag ptag LEFT JOIN tag tag2 ON ptag.tag_id = tag2.id WHERE FIND_IN_SET(tag2.name, :tags)>0) )
                        ELSE TRUE
                        END
                    AND (
                        CASE
                        WHEN :isExactMatch OR :searchKey=''
                        THEN (a.first_name LIKE CONCAT('%', :searchKey, '%') OR a.last_name LIKE CONCAT('%', :searchKey, '%')  OR p.title LIKE CONCAT('%', :searchKey, '%') OR p.modified_content LIKE CONCAT('%', :searchKey, '%'))
                        ELSE (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0 OR MATCH (p.title , p.modified_content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0)
                        END
                    )
                )
            )
            """, countQuery = """
            SELECT SUM(searchCount) FROM
                (
                    (SELECT
                        COUNT(*) AS searchCount
                     FROM
                        page_version v JOIN
                        page p ON v.page_id = p.id LEFT JOIN
                        users mb ON v.modified_by = mb.id LEFT JOIN
                        users a ON p.author = a.id LEFT JOIN
                        users lb ON p.locked_by = lb.id LEFT JOIN
                        directory dr ON dr.id = p.directory_id LEFT JOIN
                        workflow w ON w.directory_id=dr.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalComments FROM comment WHERE entity_type = 'PAGE' AND is_deleted = 0  GROUP BY entity_id) c ON c.entity_id = p.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalParentComments FROM comment WHERE entity_type = 'PAGE' AND is_deleted = 0 AND parent_comment_id IS NULL GROUP BY entity_id) cpt ON c.entity_id = p.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalRatings FROM rating WHERE entity_type = 'PAGE' AND rating = 'UP' AND is_active GROUP BY entity_id) r ON r.entity_id = p.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalDownRatings FROM rating WHERE entity_type = 'PAGE' AND rating = 'DOWN' AND is_active GROUP BY entity_id) rd ON rd.entity_id = p.id LEFT JOIN
                        (SELECT r2.page_version_id, ws2.workflow_id, COUNT(*) AS totalApprovedReviews FROM review r2 LEFT JOIN workflow_step ws2 ON ws2.id=r2.workflow_step_id WHERE r2.status = 'APPROVED' GROUP BY r2.page_version_id, ws2.workflow_id) rv ON rv.page_version_id = v.id AND rv.workflow_id=w.id LEFT JOIN
                        (SELECT r3.page_version_id, ws3.workflow_id, COUNT(*) AS totalDisapprovedReviews FROM review r3 LEFT JOIN workflow_step ws3 ON ws3.id=r3.workflow_step_id WHERE r3.status = 'DISAPPROVED' GROUP BY r3.page_version_id, ws3.workflow_id) rv2 ON rv2.page_version_id = v.id AND rv2.workflow_id=w.id LEFT JOIN
                        (SELECT page_version_id, COUNT(*) AS totalPendingReviews FROM review WHERE status = 'PENDING' GROUP BY page_version_id) rv3 ON rv3.page_version_id = v.id LEFT JOIN
                        (SELECT workflow_id, COUNT(*) AS totalRequiredApproval FROM workflow_step GROUP BY workflow_id) w3 ON w3.workflow_id = w.id LEFT JOIN
                        (SELECT pt.page_id, GROUP_CONCAT(t.name SEPARATOR '|') as pTags FROM page_tag pt LEFT JOIN tag t ON pt.tag_id = t.id GROUP BY pt.page_id) allTags ON allTags.page_id=v.page_id LEFT JOIN
                        (SELECT pcat.page_id, GROUP_CONCAT(ct.name SEPARATOR '|') as pCats FROM page_category pcat LEFT JOIN category ct ON pcat.category_id = ct.id GROUP BY pcat.page_id) allCats ON allCats.page_id=v.page_id LEFT JOIN
                        (SELECT entity_id,author,date_created FROM save WHERE entity_type='PAGE' AND author=:userId GROUP BY entity_id,author,date_created) sp ON sp.entity_id=v.page_id LEFT JOIN
                        (SELECT entity_id,user_id,rating FROM rating WHERE entity_type = 'PAGE' AND user_id = :userId AND is_active GROUP BY entity_id,user_id,rating) rrp ON rrp.entity_id=v.page_id LEFT JOIN
                        (SELECT
                            p20.id AS pageId,
                            GROUP_CONCAT(DISTINCT pr20.id) AS userPermissions,
                            GROUP_CONCAT(DISTINCT pr20.name) AS userPermissionNames
                        FROM
                            (SELECT *FROM users WHERE id = :userId) u20 LEFT JOIN
                            user_page_access upa20 ON u20.id = upa20.user_id LEFT JOIN
                            directory_user_access dua20 ON u20.id = dua20.user_id LEFT JOIN
                            group_users gu20 ON u20.id = gu20.user_id LEFT JOIN
                            (SELECT id, is_active FROM cluster  WHERE is_active) ct20 ON ct20.id = gu20.group_id LEFT JOIN
                            group_page_access gpa20 ON ct20.id = gpa20.group_id LEFT JOIN
                            directory_group_access dga20 ON ct20.id = dga20.group_id LEFT JOIN
                            page p20 ON (
                                p20.id = upa20.page_id
                                OR p20.directory_id = dga20.directory_id
                                OR p20.directory_id = dua20.directory_id
                                OR p20.id = gpa20.page_id
                            ) LEFT JOIN
                            permission pr20 ON (
                                (pr20.id = upa20.permission_id AND upa20.page_id = p20.id)
                                OR (pr20.id = dua20.permission_id AND dua20.directory_id = p20.directory_id)
                                OR (pr20.id = gpa20.permission_id AND gpa20.page_id = p20.id)
                                OR (pr20.id = dga20.permission_id AND dga20.directory_id = p20.directory_id)
                            )
                        GROUP BY p20.id) upp ON upp.pageId=v.page_id
                    WHERE
                        FIND_IN_SET(p.page_type, :pageTypeFilter)>0
                        AND p.is_deleted = 0
                        AND CASE WHEN :isArchived IS NOT NULL THEN p.is_active <> :isArchived ELSE TRUE END
                        AND CASE WHEN :author IS NOT NULL AND :author <> '' THEN a.email = :author ELSE TRUE END
                        AND CASE WHEN :savedOnly THEN sp.entity_id = p.id ELSE TRUE END
                    AND CASE WHEN :upVotedOnly THEN rrp.rating = 'UP' ELSE TRUE END
                        AND CASE WHEN :pagePrimaryKeys IS NOT NULL AND :pagePrimaryKeys <> '' THEN FIND_IN_SET(v.page_id, :pagePrimaryKeys)>0 ELSE TRUE END
                        AND CASE WHEN :parentDirectory IS NOT NULL THEN p.directory_id = :parentDirectory ELSE TRUE END
                        AND CASE WHEN :fromDate IS NOT NULL AND :fromDate <> '' THEN DATE(p.date_created) >= :fromDate ELSE TRUE END
                        AND CASE WHEN :categories IS NOT NULL AND :categories <> ''
                            THEN (v.page_id IN (SELECT pcat2.page_id FROM page_category pcat2 LEFT JOIN category cat2 ON pcat2.category_id = cat2.id WHERE FIND_IN_SET(cat2.name, :categories)>0))
                            ELSE TRUE
                            END
                        AND CASE
                            WHEN :tags IS NOT NULL AND :tags <> ''
                            THEN (v.page_id IN (SELECT ptag.page_id FROM page_tag ptag LEFT JOIN tag tag2 ON ptag.tag_id = tag2.id WHERE FIND_IN_SET(tag2.name, :tags)>0) )
                            ELSE TRUE
                            END
                        AND (
                            CASE
                            WHEN :isExactMatch OR :searchKey=''
                            THEN (a.first_name LIKE CONCAT('%', :searchKey, '%') OR a.last_name LIKE CONCAT('%', :searchKey, '%')  OR v.title LIKE CONCAT('%', :searchKey, '%') OR v.content LIKE CONCAT('%', :searchKey, '%'))
                            ELSE (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0 OR MATCH (mb.first_name , mb.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0 OR MATCH (v.title , v.content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0)
                            END
                        )
                        AND (
                            CASE
                            WHEN :isPublished OR :allVersions
                            THEN (
                                ((v.page_id , v.id) IN
                                    (SELECT pv.page_id, CASE WHEN :allVersions THEN MAX(pv.id) ELSE pv.id END
                                        FROM page_version pv
                                        WHERE EXISTS(SELECT 1 FROM
                                            (SELECT COUNT(*) AS totalApprovedReviews FROM review r2 LEFT JOIN workflow_step ws2 ON ws2.id=r2.workflow_step_id WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id AND ws2.workflow_id = w.id) rCheck
                                                WHERE rCheck.totalApprovedReviews = (SELECT MAX(step) FROM workflow_step WHERE workflow_id=w.id)
                                        )
                                        GROUP BY pv.page_id, pv.id
                                    )
                                )
                                AND (
                                    EXISTS(SELECT r10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role r10 ON ur10.role_id = r10.id WHERE r10.role_name = 'Administrator' AND u10.id = :userId)
                                    OR (
                                        (
                                            EXISTS(SELECT p10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN permission p10 ON rp10.permission_id = p10.id WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId)
                                            OR EXISTS(SELECT gp10.group_id FROM users u10 LEFT JOIN group_users gu10 ON u10.id = gu10.user_id LEFT JOIN cluster c10 ON c10.id = gu10.group_id LEFT JOIN group_permissions gp10 ON gu10.group_id = gp10.group_id LEFT JOIN permission p10 ON gp10.permission_id = p10.id WHERE p10.name = 'READ_CONTENT' AND u10.id = :userId AND c10.is_active)
                                        )
                                        AND FIND_IN_SET('READ_CONTENT', upp.userPermissionNames) > 0
                                    )
                                )
                            )
                            ELSE :isPublished
                            END
                            OR
                            CASE
                            WHEN NOT :isPublished OR :allVersions
                            THEN (
                                (v.page_id , v.id) IN
                                (
                                    SELECT pv.page_id, pv.id
                                    FROM page_version pv
                                    WHERE NOT EXISTS(
                                        SELECT 1
                                        FROM(SELECT COUNT(*) AS totalApprovedReviews FROM review r2 LEFT JOIN workflow_step ws2 ON ws2.id=r2.workflow_step_id WHERE r2.status = 'APPROVED' AND r2.page_version_id = pv.id AND ws2.workflow_id = w.id) rCheck
                                        WHERE rCheck.totalApprovedReviews = (SELECT MAX(step) FROM workflow_step WHERE workflow_id=w.id)
                                    )
                                    GROUP BY pv.page_id
                                )
                                AND (
                                    (
                                        :pendingOnly
                                        AND
                                        rv3.totalPendingReviews > 0
                                        AND (
                                            EXISTS(SELECT r10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role r10 ON ur10.role_id = r10.id WHERE r10.role_name = 'Administrator' AND u10.id = :userId)
                                            OR EXISTS(SELECT p10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN permission p10 ON rp10.permission_id = p10.id WHERE p10.name = 'CONTENT_APPROVAL' AND u10.id = :userId)
                                            OR EXISTS(SELECT gp10.group_id FROM users u10 LEFT JOIN group_users gu10 ON u10.id = gu10.user_id LEFT JOIN cluster c10 ON c10.id = gu10.group_id LEFT JOIN group_permissions gp10 ON gu10.group_id = gp10.group_id LEFT JOIN permission p10 ON gp10.permission_id = p10.id WHERE p10.name = 'CONTENT_APPROVAL' AND u10.id = :userId AND c10.is_active)
                                        )
                                        AND EXISTS(SELECT u10.id FROM users u10 LEFT JOIN workflow_step_approver wsa10 ON u10.id = wsa10.approver_id LEFT JOIN workflow_step ws10 ON wsa10.workflow_step_id=ws10.id LEFT JOIN workflow w10 ON ws10.workflow_id = w10.id WHERE u10.id = :userId AND w10.directory_id = p.directory_id)
                                    )
                                    OR (
                                        :draftOnly
                                        AND (
                                            EXISTS(SELECT r10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role r10 ON ur10.role_id = r10.id WHERE r10.role_name = 'Administrator' AND u10.id = :userId)
                                            OR EXISTS(SELECT p10.id FROM users u10 LEFT JOIN user_role ur10 ON u10.id = ur10.user_id LEFT JOIN role_permission rp10 ON ur10.role_id = rp10.role_id LEFT JOIN permission p10 ON rp10.permission_id = p10.id WHERE p10.name = 'UPDATE_CONTENT' AND u10.id = :userId)
                                            OR EXISTS(SELECT gp10.group_id FROM users u10 LEFT JOIN group_users gu10 ON u10.id = gu10.user_id LEFT JOIN cluster c10 ON c10.id = gu10.group_id LEFT JOIN group_permissions gp10 ON gu10.group_id = gp10.group_id LEFT JOIN permission p10 ON gp10.permission_id = p10.id WHERE p10.name = 'UPDATE_CONTENT' AND u10.id = :userId AND c10.is_active)
                                        )
                                        AND FIND_IN_SET('UPDATE_CONTENT', upp.userPermissionNames) > 0
                                    )
                                )
                            )
                            ELSE NOT :isPublished
                            END
                        )
                    )
                    UNION ALL
                    (SELECT
                        COUNT(*) AS searchCount
                    FROM
                        post p LEFT JOIN
                        users a ON p.author = a.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalComments FROM comment WHERE entity_type = 'POST' AND is_deleted = 0 GROUP BY entity_id) c ON c.entity_id = p.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalParentComments FROM comment WHERE entity_type = 'POST' AND is_deleted = 0 AND parent_comment_id IS NULL GROUP BY entity_id) cpt ON c.entity_id = p.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalRatings FROM rating WHERE entity_type = 'POST' AND rating = 'UP' AND is_active GROUP BY entity_id) r ON r.entity_id = p.id LEFT JOIN
                        (SELECT entity_id, COUNT(*) AS totalDownRatings FROM rating WHERE entity_type = 'POST' AND rating = 'DOWN' AND is_active GROUP BY entity_id) rd ON rd.entity_id = p.id LEFT JOIN
                        (SELECT pt.post_id, GROUP_CONCAT(t.name SEPARATOR '|') as pTags FROM post_tag pt LEFT JOIN tag t ON pt.tag_id = t.id GROUP BY pt.post_id) allTags ON allTags.post_id=p.id LEFT JOIN
                        (SELECT pcat.post_id, GROUP_CONCAT(ct.name SEPARATOR '|') as pCats FROM post_category pcat LEFT JOIN category ct ON pcat.category_id = ct.id GROUP BY pcat.post_id) allCats ON allCats.post_id=p.id LEFT JOIN
                        (SELECT entity_id,author,date_created FROM save WHERE entity_type='POST' AND author=:userId GROUP BY entity_id,author,date_created) sp ON sp.entity_id=p.id LEFT JOIN
                        (SELECT entity_id,user_id,rating FROM rating WHERE entity_type = 'POST' AND user_id = :userId AND is_active GROUP BY entity_id,user_id,rating) rrp ON rrp.entity_id=p.id
                    WHERE
                        (FIND_IN_SET('DISCUSSION', :pageTypeFilter)>0 OR FIND_IN_SET('POST', :pageTypeFilter)>0)
                        AND p.is_deleted = 0
                        AND CASE WHEN :isArchived IS NOT NULL THEN p.is_active <> :isArchived ELSE TRUE END
                        AND CASE WHEN :author IS NOT NULL AND :author <> '' THEN a.email = :author ELSE TRUE END
                        AND CASE WHEN :savedOnly THEN sp.entity_id = p.id ELSE TRUE END
                    AND CASE WHEN :upVotedOnly THEN rrp.rating = 'UP' ELSE TRUE END
                        AND CASE WHEN :pagePrimaryKeys IS NOT NULL AND :pagePrimaryKeys <> '' THEN FIND_IN_SET(p.id, :pagePrimaryKeys)>0 ELSE TRUE END
                        AND CASE WHEN :fromDate IS NOT NULL AND :fromDate <> '' THEN DATE(p.date_created) >= :fromDate ELSE TRUE END
                        AND CASE WHEN :categories IS NOT NULL AND :categories <> ''
                            THEN (p.id IN (SELECT pcat2.post_id FROM post_category pcat2 LEFT JOIN category cat2 ON pcat2.category_id = cat2.id WHERE FIND_IN_SET(cat2.name, :categories)>0))
                            ELSE TRUE
                            END
                        AND CASE
                            WHEN :tags IS NOT NULL AND :tags <> ''
                            THEN (p.id IN (SELECT ptag.post_id FROM post_tag ptag LEFT JOIN tag tag2 ON ptag.tag_id = tag2.id WHERE FIND_IN_SET(tag2.name, :tags)>0) )
                            ELSE TRUE
                            END
                        AND (
                            CASE
                            WHEN :isExactMatch OR :searchKey=''
                            THEN (a.first_name LIKE CONCAT('%', :searchKey, '%') OR a.last_name LIKE CONCAT('%', :searchKey, '%')  OR p.title LIKE CONCAT('%', :searchKey, '%') OR p.modified_content LIKE CONCAT('%', :searchKey, '%'))
                            ELSE (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0 OR MATCH (p.title , p.modified_content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0)
                            END
                        )
                    )
                ) searchRecords
                    """)
    Optional<Page<Map<String, Object>>> searchAll(
            @Param("pageTypeFilter") String pageTypeFilter,
            @Param("searchKey") String searchKey,
            @Param("isExactMatch") Boolean isExactMatch,
            @Param("isArchived") Boolean isArchived,
            @Param("isPublished") Boolean isPublished,
            @Param("allVersions") Boolean allVersions,
            @Param("categories") String categories,
            @Param("tags") String tags,
            @Param("userId") Long userId,
            @Param("pagePrimaryKeys") String pageIds,
            @Param("parentDirectory") Long directoryId,
            @Param("pendingOnly") Boolean pendingOnly,
            @Param("draftOnly") Boolean draftOnly,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("author") String author,
            @Param("savedOnly") Boolean savedOnly,
            @Param("upVotedOnly") Boolean upVotedOnly,
            Pageable pageable);
}
