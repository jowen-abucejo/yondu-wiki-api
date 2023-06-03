package com.yondu.knowledgebase.repositories;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yondu.knowledgebase.entities.Page;

public interface PageRepository extends JpaRepository<Page, Long> {
    @Query(value = "SELECT DISTINCT p FROM Page p JOIN FETCH p.pageVersions v LEFT JOIN FETCH p.author a " +
            "LEFT JOIN FETCH v.modifiedBy m LEFT JOIN v.reviews r JOIN p.tags t " +
            "JOIN p.categories c WHERE p.deleted =:deleted AND r.status  =:pageVersionReviewsStatus " +
            "AND c.name IN :categories AND t.name IN :tags", countQuery = "SELECT COUNT(p) FROM "
                    + "Page p JOIN p.pageVersions v LEFT JOIN p.author a LEFT JOIN v.modifiedBy m LEFT JOIN "
                    + " v.reviews r WHERE p.deleted = :deleted AND r.status = :pageVersionReviewsStatus")
    public Optional<org.springframework.data.domain.Page<Page>> findByCategoriesInAndTagsInAndDeletedAndPageVersionsReviewsStatus(
            String[] categories,
            String[] tags, boolean deleted, String pageVersionReviewsStatus, Pageable paging);

    @Query(value = "SELECT DISTINCT p FROM Page p JOIN FETCH p.pageVersions v LEFT JOIN FETCH p.author a " +
            "LEFT JOIN FETCH v.modifiedBy m LEFT JOIN v.reviews r JOIN p.tags t " +
            "WHERE p.deleted =:deleted AND r.status =:pageVersionReviewsStatus " +
            "AND t.name IN :tags", countQuery = "SELECT COUNT(p) FROM "
                    + "Page p JOIN p.pageVersions v LEFT JOIN p.author a LEFT JOIN v.modifiedBy m LEFT JOIN "
                    + " v.reviews r WHERE p.deleted = :deleted AND r.status = :pageVersionReviewsStatus")
    public Optional<org.springframework.data.domain.Page<Page>> findByTagsInAndDeletedAndPageVersionsReviewsStatus(
            String[] tags,
            boolean deleted, String pageVersionReviewsStatus,
            Pageable paging);

    @Query(value = "SELECT DISTINCT p FROM Page p JOIN FETCH p.pageVersions v LEFT JOIN FETCH p.author a " +
            "LEFT JOIN FETCH v.modifiedBy m LEFT JOIN v.reviews r JOIN p.categories c " +
            "WHERE p.deleted =:deleted AND r.status =:pageVersionReviewsStatus AND c.name IN :categories", countQuery = "SELECT COUNT(p) FROM "
                    + "Page p JOIN p.pageVersions v LEFT JOIN p.author a LEFT JOIN v.modifiedBy m LEFT JOIN "
                    + " v.reviews r WHERE p.deleted = :deleted AND r.status = :pageVersionReviewsStatus")

    public Optional<org.springframework.data.domain.Page<Page>> findByCategoriesInAndDeletedAndPageVersionsReviewsStatus(
            String[] categories, boolean deleted, String pageVersionReviewsStatus,
            Pageable paging);

    @Query(value = "SELECT DISTINCT p FROM Page p JOIN FETCH p.pageVersions v LEFT JOIN FETCH p.author a " +
            "LEFT JOIN FETCH v.modifiedBy m LEFT JOIN v.reviews r " +
            "WHERE p.deleted =:deleted AND r.status =:pageVersionReviewsStatus", countQuery = "SELECT COUNT(p) FROM "
                    + "Page p JOIN p.pageVersions v LEFT JOIN p.author a LEFT JOIN v.modifiedBy m LEFT JOIN "
                    + " v.reviews r WHERE p.deleted = :deleted AND r.status = :pageVersionReviewsStatus")
    public Optional<org.springframework.data.domain.Page<Page>> findByDeletedAndPageVersionsReviewsStatus(
            @Param("deleted") boolean deleted,
            @Param("pageVersionReviewsStatus") String pageVersionReviewsStatus, Pageable paging);

    public Optional<Page> findByIdAndActive(Long id, Boolean isActive);

    public Optional<Page> findByIdAndDeleted(Long id, Boolean isDeleted);
}
