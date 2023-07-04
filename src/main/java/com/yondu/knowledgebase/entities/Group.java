package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "cluster")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_users", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(name = "group_rights", joinColumns = @JoinColumn(name =
    // "group_id"), inverseJoinColumns = @JoinColumn(name = "rights_id"))
    // private Set<Rights> rights;

    @ManyToMany
    @JoinTable(name = "group_permissions", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GroupPageAccess> groupPageAccess = new HashSet<>();

    public Group() {
    }

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
        this.users = new HashSet<>();
        // this.rights = new HashSet<>();
        this.groupPageAccess = new HashSet<>();
        this.permissions = new HashSet<>();
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // public Set<Rights> getRights() {
    // return rights;
    // }

    // public void setRights(Set<Rights> rights) {
    // this.rights = rights;
    // }

    public Set<GroupPageAccess> getGroupPageAccess() {
        return groupPageAccess;
    }

    public void setGroupPageAccess(Set<GroupPageAccess> groupPageAccess) {
        this.groupPageAccess = groupPageAccess;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
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
        // Compare fields for equality
        return this.id.equals(other.id);
    }
}
