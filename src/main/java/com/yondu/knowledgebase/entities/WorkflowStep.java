package com.yondu.knowledgebase.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class WorkflowStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int step;
    @ManyToOne(fetch = FetchType.EAGER)
    private Workflow workflow;
    @OneToMany(mappedBy = "workflowStep", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<WorkflowStepApprover> approvers = new HashSet<>();

    public WorkflowStep() {}

    public WorkflowStep(Workflow workflow, String name, int step) {
        this.workflow = workflow;
        this.name = name;
        this.step = step;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Set<WorkflowStepApprover> getApprovers() {
        return approvers;
    }

    public void setApprovers(Set<WorkflowStepApprover> approvers) {
        this.approvers = approvers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WorkflowStep other = (WorkflowStep) obj;
        return this.id.equals(other.id);
    }
}
