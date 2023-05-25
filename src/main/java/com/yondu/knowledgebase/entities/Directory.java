package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

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

    public Directory() {
    }

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
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

    @Override
    public String toString() {
        Directory currentDirectory = this.parent;
        StringBuilder directory = new StringBuilder(this.name);
        while(currentDirectory != null) {
            directory.insert(0, currentDirectory.name + "/");
            currentDirectory = currentDirectory.parent;
        }
        return directory.toString();
    }
}
