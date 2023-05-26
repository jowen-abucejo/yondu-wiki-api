package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.UserDirectoryAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDirectoryAccessRepository extends JpaRepository<UserDirectoryAccess, Long> {
    Optional<UserDirectoryAccess> findById(Long id);
}
