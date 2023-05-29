package com.yondu.knowledgebase.entities;

import java.util.Set;

import jakarta.persistence.*;

@Entity
public class DirectoryPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private Boolean isDeleted;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryRoleAccess> directoryRoleAccesses;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryUserAccess> directoryUserAccesses;

    public DirectoryPermission() {
    }

    public DirectoryPermission(String name, String description) {
        this.name = name;
        this.description = description;
        this.isDeleted = false;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Set<DirectoryRoleAccess> getDirectoryRoleAccesses() {
        return directoryRoleAccesses;
    }

    public void setDirectoryRoleAccesses(Set<DirectoryRoleAccess> directoryRoleAccesses) {
        this.directoryRoleAccesses = directoryRoleAccesses;
    }

    public Set<DirectoryUserAccess> getDirectoryUserAccesses() {
        return directoryUserAccesses;
    }

    public void setDirectoryUserAccesses(Set<DirectoryUserAccess> directoryUserAccesses) {
        this.directoryUserAccesses = directoryUserAccesses;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DirectoryPermission other = (DirectoryPermission) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }


    @Override
    public String toString() {
        return "DirectoryPermission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isDeleted=" + isDeleted +
                ", directoryRoleAccesses=" + directoryRoleAccesses +
                ", DirectoryUserAccesses=" + directoryUserAccesses +
                '}';
    }
}
