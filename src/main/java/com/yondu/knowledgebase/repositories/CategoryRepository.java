package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Category;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    public Set<Category> findByNameIn(List<String> categories);
    @Query("SELECT c FROM Category c ORDER BY c.deleted ASC, c.name ASC")
    Page<Category> searchCategories(Pageable pageable);
}
