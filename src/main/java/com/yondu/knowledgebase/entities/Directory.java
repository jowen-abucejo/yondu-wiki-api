package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "directories")
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Directory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Directory> subdirectories;

    public Directory() {
    }

    public Directory(String name, Directory parent, Set<Directory> subdirectories) {
        this.name = name;
        this.parent = parent;
        this.subdirectories = subdirectories;
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

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public Set<Directory> getSubdirectories() {
        return subdirectories;
    }

    public void setSubdirectories(Set<Directory> subdirectories) {
        this.subdirectories = subdirectories;
    }
}
