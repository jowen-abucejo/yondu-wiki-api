package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectoryUserAccessRepository extends JpaRepository<DirectoryUserAccess, Long> {
}
