package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.DirectoryRoleAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectoryRoleAccessRepository extends JpaRepository<DirectoryRoleAccess, Long> {
}
