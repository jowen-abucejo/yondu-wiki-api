package com.yondu.knowledgebase.repositories;

import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.WorkflowStep;
import com.yondu.knowledgebase.entities.WorkflowStepApprover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowStepApproverRepository extends JpaRepository<WorkflowStepApprover, Long> {
    Optional<WorkflowStepApprover> findByApproverAndWorkflowStep(User user, WorkflowStep workflowStep);
    List<WorkflowStepApprover> findAllByWorkflowStep(WorkflowStep workflowStep);
}
