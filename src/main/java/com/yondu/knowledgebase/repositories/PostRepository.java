package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE (:searchKey IS NULL OR :searchKey = '' OR p.title LIKE %:searchKey% OR p.content LIKE %:searchKey% OR p.author.username LIKE %:searchKey% OR p.author.email LIKE %:searchKey% OR CONCAT(p.author.firstName, ' ', p.author.lastName) LIKE %:searchKey% OR EXISTS (SELECT t FROM p.tags t WHERE t.name LIKE %:searchKey%) OR EXISTS (SELECT c FROM p.categories c WHERE c.name LIKE %:searchKey%)) ORDER BY p.dateCreated DESC")
    Page<Post> searchPosts(@Param("searchKey") String searchKey, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN Comment c ON p.id = c.entityId AND c.entityType = 'POST' " +
            "LEFT JOIN Rating r ON p.id = r.entity_id AND r.entity_type = 'POST' AND r.rating = 'UP' " +
            "WHERE p.active = true AND p.deleted = false " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(DISTINCT c.id) + COUNT(DISTINCT r.id) DESC")
    List<Post> findMostPopularPosts(Pageable pageable);
}
