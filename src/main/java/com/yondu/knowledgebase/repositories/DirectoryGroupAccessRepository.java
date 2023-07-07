package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryGroupAccess;
import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DirectoryGroupAccessRepository extends JpaRepository<DirectoryGroupAccess, Long> {
    Optional<DirectoryGroupAccess> findByDirectoryAndPermissionAndGroup(Directory directory, Permission permission, Group group);
}
