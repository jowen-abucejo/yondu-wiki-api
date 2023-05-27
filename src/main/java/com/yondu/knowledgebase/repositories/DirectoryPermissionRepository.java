package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.DirectoryPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DirectoryPermissionRepository extends JpaRepository<DirectoryPermission, Long> {
    List<DirectoryPermission> findByIsDeletedFalse();
    Optional<DirectoryPermission> findByIdAndIsDeletedFalse(Long id);
}
