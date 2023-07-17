package com.yondu.knowledgebase.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yondu.knowledgebase.entities.Post;
import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p, COUNT(DISTINCT c.id) AS commentCount, COUNT(DISTINCT r.id) AS upVoteCount " +
            "FROM Post p " +
            "LEFT JOIN Comment c ON p.id = c.entityId AND c.entityType = 'POST' " +
            "LEFT JOIN Rating r ON p.id = r.entity_id AND r.entity_type = 'POST' AND r.rating = 'UP' " +
            "WHERE (:searchKey IS NULL OR :searchKey = '' OR p.title LIKE %:searchKey% OR p.modifiedContent LIKE %:searchKey% OR p.author.username LIKE %:searchKey% OR p.author.email LIKE %:searchKey% OR CONCAT(p.author.firstName, ' ', p.author.lastName) LIKE %:searchKey% OR EXISTS (SELECT t FROM p.tags t WHERE t.name LIKE %:searchKey%) OR EXISTS (SELECT c FROM p.categories c WHERE c.name LIKE %:searchKey%)) "
            +
            "AND p.active = true AND p.deleted = false " +
            "GROUP BY p.id " +
            "ORDER BY p.dateCreated DESC")
    Page<Object[]> searchPostsWithCommentAndUpvoteCounts(@Param("searchKey") String searchKey, Pageable pageable);

    @Query("SELECT p, COUNT(DISTINCT c.id) AS commentCount, COUNT(DISTINCT r.id) AS upVoteCount, COUNT(DISTINCT r2.id) AS totalRatingCount " +
            "FROM Post p " +
            "LEFT JOIN Comment c ON p.id = c.entityId AND c.entityType = 'POST' " +
            "LEFT JOIN Rating r ON p.id = r.entity_id AND r.entity_type = 'POST' AND r.rating = 'UP' " +
            "LEFT JOIN Rating r2 ON p.id = r2.entity_id AND r2.entity_type = 'POST' " +
            "WHERE p.id = :id AND p.active = true AND p.deleted = false " +
            "GROUP BY p")
    List<Object[]> findPostWithCommentAndUpvoteCountsById(@Param("id") Long id);

    @Query("SELECT p, COUNT(DISTINCT c.id) AS commentCount, COUNT(DISTINCT r.id) AS upVoteCount " +
            "FROM Post p " +
            "LEFT JOIN Comment c ON p.id = c.entityId AND c.entityType = 'POST' " +
            "LEFT JOIN Rating r ON p.id = r.entity_id AND r.entity_type = 'POST' AND r.rating = 'UP' " +
            "WHERE p.active = true AND p.deleted = false " +
            "AND (:startDate IS NULL OR p.dateCreated >= :startDate) " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(DISTINCT c.id) DESC")
    List<Object[]> findMostPopularPosts(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT p, COUNT(DISTINCT c.id) AS commentCount, COUNT(DISTINCT r.id) AS upVoteCount " +
            "FROM Post p " +
            "LEFT JOIN Comment c ON p.id = c.entityId AND c.entityType = 'POST' " +
            "LEFT JOIN Rating r ON p.id = r.entity_id AND r.entity_type = 'POST' AND r.rating = 'UP' " +
            "WHERE p.active = true AND p.deleted = false " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(DISTINCT r.id) DESC")
    List<Object[]> findTopPosts(Pageable pageable);

    @Query("SELECT p, COUNT(DISTINCT c.id) AS commentCount, COUNT(DISTINCT r.id) AS upVoteCount " +
            "FROM Post p " +
            "LEFT JOIN Comment c ON p.id = c.entityId AND c.entityType = 'POST' AND c.isDeleted = false " +
            "LEFT JOIN Rating r ON p.id = r.entity_id AND r.entity_type = 'POST' AND r.rating = 'UP' " +
            "WHERE (p.author.id = :id) " +
            "AND (:searchKey IS NULL OR :searchKey = '' OR p.title LIKE %:searchKey% OR p.modifiedContent LIKE %:searchKey% OR p.author.username LIKE %:searchKey% OR p.author.email LIKE %:searchKey% OR CONCAT(p.author.firstName, ' ', p.author.lastName) LIKE %:searchKey% OR EXISTS (SELECT t FROM p.tags t WHERE t.name LIKE %:searchKey%) OR EXISTS (SELECT c FROM p.categories c WHERE c.name LIKE %:searchKey%)) "
            +
            "AND (:active IS NULL OR p.active = :active) " +
            "AND (:deleted IS NULL OR p.deleted = :deleted) " +
            "GROUP BY p.id " +
            "ORDER BY p.dateCreated DESC")
    Page<Object[]> searchPostsWithCommentAndUpvoteCounts(
            @Param("searchKey") String searchKey,
            @Param("active") Boolean active,
            @Param("deleted") Boolean deleted,
            @Param("id") Long id,
            Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT
                p.id AS postId,
                CASE
                WHEN
                NOT :isExactMatch OR NOT :searchKey=''
                THEN
                ROUND((
                    (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.2) +
                    (MATCH (p.title , p.modified_content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) * 0.8)),3)
                ELSE 1.0
                END AS relevance,
                COALESCE(c.totalComments, 0) AS totalComments,
                COALESCE(r.totalRatings, 0) AS totalRatings,
                p.date_created as dateCreated,
                p.date_modified as dateModified
             FROM
                post p LEFT JOIN
                (SELECT entity_id, COUNT(*) AS totalComments FROM comment WHERE entity_type = 'POST' GROUP BY entity_id) c ON c.entity_id = p.id LEFT JOIN
                (SELECT entity_id, COUNT(*) AS totalRatings FROM rating WHERE entity_type = 'POST' AND rating = 'UP' GROUP BY entity_id) r ON r.entity_id = p.id LEFT JOIN
                users a ON p.author = a.id
            WHERE
                p.is_deleted = 0
                AND (:authorId IS NULL OR p.author=:authorId)
                AND p.is_active <> :isArchived
                AND CASE
                    WHEN :categories IS NOT NULL AND :categories <> ''
                    THEN
                        (p.id IN
                            (SELECT pcat2.post_id FROM post_category pcat2
                                LEFT JOIN category cat2 ON pcat2.category_id = cat2.id
                                WHERE FIND_IN_SET(cat2.name, :categories)>0)
                            )
                    ELSE TRUE
                    END
                AND CASE
                    WHEN :tags IS NOT NULL AND :tags <> ''
                    THEN
                        (p.id IN (SELECT ptag.post_id FROM
                            post_tag ptag LEFT JOIN
                            tag tag2 ON ptag.tag_id = tag2.id
                            WHERE FIND_IN_SET(tag2.name, :tags)>0)
                            )
                    ELSE TRUE
                    END
                AND (
                    CASE
                    WHEN :isExactMatch OR :searchKey=''
                    THEN
                        (a.first_name LIKE CONCAT('%', :searchKey, '%')
                            OR a.last_name LIKE CONCAT('%', :searchKey, '%')
                            OR p.title LIKE CONCAT('%', :searchKey, '%')
                            OR p.modified_content LIKE CONCAT('%', :searchKey, '%'))
                    ELSE (MATCH (a.first_name , a.last_name) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0
                        OR MATCH (p.title , p.modified_content) AGAINST (:searchKey IN NATURAL LANGUAGE MODE) > 0)
                    END)
            """)
    Optional<Page<Map<String, Object>>> findByFullTextSearch(
            @Param("searchKey") String searchKey,
            @Param("isExactMatch") Boolean isExactMatch,
            @Param("isArchived") Boolean isArchived,
            @Param("categories") String categories,
            @Param("tags") String tags,
            @Param("authorId") Long authorId,
            Pageable pageable);
}
