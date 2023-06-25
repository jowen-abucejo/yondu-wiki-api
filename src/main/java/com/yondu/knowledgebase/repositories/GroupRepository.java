package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.Rights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByName(String name);
    @Query("SELECT g.rights FROM cluster g WHERE g.id = :userGroupId ")
    Set<Rights> findRightsById(Long userGroupId);

    @Query("SELECT g FROM cluster g WHERE g.id = :userGroupId AND g.isActive = :isActive ")
    Optional<Group> findByIdAndIsActive(Long userGroupId, Boolean isActive);
}
