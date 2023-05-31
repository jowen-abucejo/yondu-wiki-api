package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository <Category, Long> {

    boolean existsByName(String name);
}

