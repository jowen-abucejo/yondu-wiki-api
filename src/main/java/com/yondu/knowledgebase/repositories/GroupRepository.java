package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.Permission;
import com.yondu.knowledgebase.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByName(String name);
    @Query("SELECT g FROM cluster g WHERE g.id = :userGroupId AND g.isActive = :isActive ")
    Optional<Group> findByIdAndIsActive(Long userGroupId, Boolean isActive);

    @Query("SELECT g FROM cluster g WHERE g.name LIKE %:searchKey% AND g.isActive = :isActive ")
    Page<Group> findAllByNameAndStatus(String searchKey, boolean isActive, PageRequest pageRequest);

    @Query("SELECT g FROM cluster g WHERE g.isActive = :isActive ")
    Page<Group> findAllByStatus(boolean isActive, PageRequest pageRequest);

    @Query("SELECT g FROM cluster g WHERE g.name  LIKE %:searchKey% ")
    Page<Group> findAllByName(String searchKey, PageRequest pageRequest);

    @Query("""
            SELECT g FROM cluster g
            JOIN g.permissions p
            WHERE g.name LIKE ?1
            AND p = ?2
            AND g.isActive = '1'
            """)
    Page<Group> findAllByNameAndPermission(String searchKey, Permission permission, Pageable pageable);

    @Query("""
            SELECT g FROM cluster g
            JOIN g.users u
            WHERE u = ?1
            """)
    List<Group> findAllGroupsByUser(User user);
}
