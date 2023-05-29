package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.PageVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageVersionRepository extends JpaRepository<PageVersion, Long> {
}
