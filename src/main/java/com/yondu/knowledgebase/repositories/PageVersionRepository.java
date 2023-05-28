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
    @Query("SELECT v FROM PageVersion v JOIN Page p WHERE p.deleted=false AND (FUNCTION('REGEXP_REPLACE', v.title, '<[^>]+>', '') LIKE %:searchKey% OR FUNCTION('REGEXP_REPLACE', v.content, '<[^>]+>', '') LIKE %:searchKey%)")
    Optional<Page<PageVersion>> findByTitleOrContent(String searchKey, Pageable paging);
}
