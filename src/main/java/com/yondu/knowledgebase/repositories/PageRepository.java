package com.yondu.knowledgebase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yondu.knowledgebase.entities.Page;

public interface PageRepository extends JpaRepository<Page, Long> {

    @EntityGraph(attributePaths = { "pageVersions.modifiedBy" })
    @Query(value = "SELECT * from page p INNER JOIN page_version v WHERE p.id=:id AND page_version.date_modified=(SELECT MAX(date_modified) FROM version))", nativeQuery = true)

    public Optional<Page> findById(Long id);

    @EntityGraph(attributePaths = { "pageVersions.modifiedBy" })
    public List<Page> findAll();
}
