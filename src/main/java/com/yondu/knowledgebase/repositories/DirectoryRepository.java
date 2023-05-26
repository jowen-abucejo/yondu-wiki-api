package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    Optional<Directory> findById (Long id);
}
