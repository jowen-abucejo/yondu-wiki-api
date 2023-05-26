package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.DirectoryPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirectoryPermissionRepository extends JpaRepository<DirectoryPermission, Long> {
    Optional<DirectoryPermission> findById (Long id);
}
