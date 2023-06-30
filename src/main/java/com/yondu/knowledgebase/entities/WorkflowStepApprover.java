package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

@Entity
public class WorkflowStepApprover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private WorkflowStep workflowStep;
    @ManyToOne(fetch = FetchType.EAGER)
    private User approver;

    public WorkflowStepApprover () {}

    public WorkflowStepApprover(WorkflowStep workflowStep, User approver) {
        this.workflowStep = workflowStep;
        this.approver = approver;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkflowStep getWorkflowStep() {
        return workflowStep;
    }

    public void setWorkflowStep(WorkflowStep workflowStep) {
        this.workflowStep = workflowStep;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WorkflowStepApprover other = (WorkflowStepApprover) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
