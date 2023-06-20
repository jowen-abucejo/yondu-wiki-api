package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findAllByCategory(String category);

    List<Permission> findAllByCategoryOrCategoryOrCategoryOrCategory(String category1, String category2, String category3, String category4);
}
