package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.WorkflowStepApprover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowStepApproverRepository extends JpaRepository<WorkflowStepApprover, Long> {
}
