package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectoryUserAccessRepository extends JpaRepository<DirectoryUserAccess, Long> {
    boolean existsByUserAndPermissionAndDirectory(User user, Permission permission, Directory directory);
    List<DirectoryUserAccess> findByDirectoryId(Long directoryId);
}
