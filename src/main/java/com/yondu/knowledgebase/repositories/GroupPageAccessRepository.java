package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Group;
import com.yondu.knowledgebase.entities.GroupPageAccess;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface GroupPageAccessRepository extends JpaRepository<GroupPageAccess, Long> {
    Optional<GroupPageAccess> findByPageAndGroupAndPermission(Page pageId, Group groupId, Permission rightsId);

    Set<GroupPageAccess> findByGroupAndPage(Group group, Page page);

    Set<GroupPageAccess> findByGroup(Group group);

    Set<GroupPageAccess> findByPage(Page page);

}
