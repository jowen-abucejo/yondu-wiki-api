package com.yondu.knowledgebase.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DirectoryRights extends Rights{
    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DirectoryRights other = (DirectoryRights) obj;
        //Compare fields for equality
        return this.id.equals(other.id);
    }
}
