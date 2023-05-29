package com.yondu.knowledgebase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yondu.knowledgebase.entities.Page;

public interface PageRepository extends JpaRepository<Page, Long> {

        @EntityGraph(attributePaths = { "pageVersions.modifiedBy" })
        @Query(value = "SELECT * from page p INNER JOIN page_version v ON p.id=v.page_id "
                        + "WHERE p.id=:id AND v.date_modified=(SELECT MAX(v2.date_modified) FROM page_version v2)", nativeQuery = true)
        public Optional<Page> findById(Long id);

        @Query(value = "SELECT * from page p INNER JOIN directory d ON d.id=p.directory_id " +
                        "INNER JOIN page_version v ON v.page_id=p.id WHERE p.directory_id=:directoryId AND p.id=:pageId "
                        +
                        "AND v.date_modified=(SELECT MAX(v2.date_modified) FROM page_version v2)", nativeQuery = true)
        public Optional<Page> findByDirectoryIdAndId(Long directoryId, Long pageId);

        // @EntityGraph(attributePaths = { "pageVersions.modifiedBy", "author" })
        // public org.springframework.data.domain.Page<Page> findAll(String searchKey,
        // Pageable paging);
}
