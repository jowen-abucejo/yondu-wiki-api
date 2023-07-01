package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.DirectoryUserAccess;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DirectoryUserAccessRepository extends JpaRepository<DirectoryUserAccess, Long> {

    @Query("SELECT d FROM DirectoryUserAccess d WHERE directory = ?1 AND permission = ?2 AND user = ?3")
    public Optional<DirectoryUserAccess> findByDirectoryAndPermissionAndUser(Directory dir, Permission permission, User user);
}
