package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "parent_id"})})
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private User createdBy;

    @Column(nullable = false)
    private LocalDate dateCreated;

    @Column(nullable = false)
    private LocalDate dateModified;

    @ManyToOne(fetch = FetchType.EAGER)
    private Directory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Directory> subDirectories;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Page> pages;

    @OneToMany(mappedBy = "directory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DirectoryRights> rights;

    public Directory() {}

    // creation of subdirectories
    public Directory(String name, String description, Directory parent, User createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.dateCreated = LocalDate.now();
        this.dateModified = LocalDate.now();
        this.parent = parent;
        this.rights = new HashSet<>();
        this.subDirectories = new HashSet<>();
        this.pages = new HashSet<>();
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

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }


    public Set<Directory> getSubDirectories() {
        return subDirectories;
    }

    public void setSubDirectories(Set<Directory> subDirectories) {
        this.subDirectories = subDirectories;
    }

    public Set<Page> getPages() {
        return pages;
    }

    public void setPages(Set<Page> pages) {
        this.pages = pages;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<DirectoryRights> getRights() {
        return rights;
    }

    public void setRights(Set<DirectoryRights> rights) {
        this.rights = rights;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Directory other = (Directory) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }
}
