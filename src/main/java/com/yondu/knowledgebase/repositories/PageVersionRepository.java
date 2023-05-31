package com.yondu.knowledgebase.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yondu.knowledgebase.entities.PageVersion;

public interface PageVersionRepository extends JpaRepository<PageVersion, Long> {

        @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
        public Optional<PageVersion> findTopByPageIdAndPageDeletedAndReviewsStatusOrderByDateModifiedDesc(
                        Long id, boolean isDeleted, String status);

        @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
        @Query("SELECT v FROM PageVersion v JOIN v.page p LEFT JOIN v.reviews r LEFT JOIN p.tags t LEFT JOIN p.categories c LEFT JOIN v.modifiedBy mb "
                        + "WHERE p.deleted=:deleted AND r.status=:reviewStatus AND (c.name LIKE %:searchKey% OR t.name LIKE %:searchKey% OR "
                        + "p.author.firstName LIKE %:searchKey% OR p.author.lastName LIKE %:searchKey% OR FUNCTION('REGEXP_REPLACE', "
                        + "v.title, '<[^>]+>', '') LIKE %:searchKey% OR FUNCTION('REGEXP_REPLACE', v.content, '<[^>]+>', '') LIKE %:searchKey%)")
        public Optional<Page<PageVersion>> findByTitleOrContent(String searchKey, boolean deleted, String reviewStatus,
                        Pageable paging);

        @EntityGraph(attributePaths = { "page.author", "modifiedBy" })
        public Optional<PageVersion> findByPageIdAndId(Long pageId, Long id);

        @EntityGraph(attributePaths = { "page" })
        public Optional<PageVersion> findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(Long id, boolean isDeleted);
}
