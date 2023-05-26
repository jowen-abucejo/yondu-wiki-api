package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.RoleDirectoryAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleDirectoryAccessRepository extends JpaRepository<RoleDirectoryAccess, Long> {
    Optional<RoleDirectoryAccess> findById(Long id);
}
