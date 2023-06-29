package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.Workflow;
import com.yondu.knowledgebase.entities.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {
    List<WorkflowStep> findByWorkflowId(Long workflowId);
    Optional<WorkflowStep> findByWorkflowAndStep(Workflow workflow, int step);
}
