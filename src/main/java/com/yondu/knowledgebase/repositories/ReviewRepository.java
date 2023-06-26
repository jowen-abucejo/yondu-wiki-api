package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.Review;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    Page<Review> findAllByStatus (ReviewStatus status, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE lower(r.pageVersion.title) like %:title%")
    Page<Review> findAllByPageVersionTitle(String title, Pageable pageable);

    Page<Review> findAll (Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.pageVersion.id = :pageVersionId " +
            "AND r.pageVersion.page.id = :pageId " +
            "AND r.status = 'PENDING'")
    Review getByPageVersionIdAndPageVersionPageId(Long pageVersionId, Long pageId);

    boolean existsByPageVersionAndStatus(PageVersion pageVersion, String status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Review r " +
            "WHERE r.pageVersion = :pageVersion " +
            "AND r.status = 'APPROVED' " +
            "AND r.user = :user")
    boolean hasUserApprovedContentInPageVersion(PageVersion pageVersion, User user);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Review r " +
            "WHERE r.pageVersion = :pageVersion " +
            "AND r.user = :user " +
            "AND r.status = 'DISAPPROVED'")
    boolean hasUserReviewedContentAsDisapprovedInPageVersion(PageVersion pageVersion, User user);
}
