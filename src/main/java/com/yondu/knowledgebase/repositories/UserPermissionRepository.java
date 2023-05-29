package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends JpaRepository<Permission, Long> {
}
