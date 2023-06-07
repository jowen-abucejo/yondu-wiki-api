package com.yondu.knowledgebase.repositories;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yondu.knowledgebase.entities.Page;

public interface PageRepository extends JpaRepository<Page, Long> {

    public Optional<Page> findByIdAndActive(Long id, Boolean isActive);

    public Optional<Page> findByIdAndDeleted(Long id, Boolean isDeleted);

}
