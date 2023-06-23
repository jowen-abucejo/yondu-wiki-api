package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {
    List<WorkflowStep> findByWorkflowId(Long workflowId);
}
