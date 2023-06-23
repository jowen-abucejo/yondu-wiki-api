package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    Workflow findByDirectoryId(Long directoryId);
}
