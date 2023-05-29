package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirectoryUserAccessRepository extends JpaRepository<DirectoryUserAccess, Long> {
    Optional<DirectoryUserAccess> findByDirectoryIdAndId (Long directoryId, Long directoryUserAccessId);
}
