package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name="cluster")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_users", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_rights", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "rights_id"))
    private Set<Rights> rights;

    public Group() {
    }

    public Group(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.users = new HashSet<>();
        this.rights = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Set<Rights> getRights() {
        return rights;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Group other = (Group) obj;
        //Compare fields for equality
        return this.id.equals(other.id);
    }
}
