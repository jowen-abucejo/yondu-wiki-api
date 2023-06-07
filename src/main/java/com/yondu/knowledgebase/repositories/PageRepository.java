package com.yondu.knowledgebase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Review;

public interface PageRepository extends JpaRepository<Page, Long> {
    @Query("SELECT DISTINCT p FROM Page p LEFT JOIN FETCH p.pageVersions pv LEFT JOIN pv.reviews r WHERE p.id = :id AND p.deleted = :deleted AND r.status IN :reviewsStatus")
    public Optional<Page> findTopByIdAndDeletedAndPageVersionsReviewsStatusIn(
            @Param("id") Long pageId,
            @Param("deleted") boolean deleted,
            @Param("reviewsStatus") List<Review.Status> reviewsStatus);

    @Query("SELECT DISTINCT p FROM Page p LEFT JOIN FETCH p.pageVersions pv LEFT JOIN pv.reviews r WHERE p.id = :id AND p.deleted = :deleted AND (r.status IN :reviewsStatus OR pv.reviews IS EMPTY)")
    public Optional<Page> findTopByIdAndDeletedAndPageVersionsReviewsStatusInOrPageVersionsReviewsIsEmpty(
            @Param("id") Long pageId,
            @Param("deleted") boolean deleted,
            @Param("reviewsStatus") List<Review.Status> reviewsStatus);

    public Optional<Page> findByIdAndActive(Long id, Boolean isActive);

    public Optional<Page> findByIdAndDeleted(Long id, Boolean isDeleted);

    @EntityGraph(attributePaths = { "directory" })
    public Optional<Page> findById(Long id);

}
