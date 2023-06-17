package com.yondu.knowledgebase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yondu.knowledgebase.entities.Page;

public interface PageRepository extends JpaRepository<Page, Long> {
        @Query("SELECT DISTINCT p FROM Page p LEFT JOIN FETCH p.pageVersions pv LEFT JOIN pv.reviews r WHERE p.id = :id AND p.type = :pageType AND p.deleted = :deleted AND r.status IN :reviewsStatus")
        public Optional<Page> findTopByIdAndTypeAndDeletedAndPageVersionsReviewsStatusIn(
                        @Param("id") Long pageId,
                        @Param("pageType") String pageType,
                        @Param("deleted") boolean deleted,
                        @Param("reviewsStatus") List<String> reviewsStatus);

        @Query("SELECT DISTINCT p FROM Page p LEFT JOIN FETCH p.pageVersions pv LEFT JOIN pv.reviews r WHERE p.id = :id ANd p.type = :pageType AND p.deleted = :deleted AND (r.status IN :reviewsStatus OR pv.reviews IS EMPTY)")
        public Optional<Page> findTopByIdAndTypeAndDeletedAndPageVersionsReviewsStatusInOrPageVersionsReviewsIsEmpty(
                        @Param("id") Long pageId,
                        @Param("pageType") String pageType,
                        @Param("deleted") boolean deleted,
                        @Param("reviewsStatus") List<String> reviewsStatus);

        public Optional<Page> findByIdAndActive(Long id, Boolean isActive);

        public Optional<Page> findByIdAndTypeAndActive(Long id, String pageType, Boolean isActive);

        public Optional<Page> findByIdAndTypeAndDeleted(Long id, String pageType, Boolean isDeleted);

        @EntityGraph(attributePaths = { "directory" })
        public Optional<Page> findById(Long id);

        @EntityGraph(attributePaths = { "directory" })
        public Optional<Page> findByIdAndType(Long id, String pageType);

}
