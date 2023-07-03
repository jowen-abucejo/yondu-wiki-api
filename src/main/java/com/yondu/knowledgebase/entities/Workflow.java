package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Directory directory;
    @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<WorkflowStep> steps;

    public Workflow() {}

    public Workflow(Directory directory) {
        this.directory = directory;
        this.steps = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Set<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(Set<WorkflowStep> steps) {
        this.steps = steps;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Workflow other = (Workflow) obj;
        return this.id.equals(other.id);
    }
}
