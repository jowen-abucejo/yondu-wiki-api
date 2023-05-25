package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity(name="users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String status;
    private LocalDate createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPagePermission> userPagePermisisons = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommentRating> userCommentRating = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comment = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> role = new HashSet<>();


    public User(Long id, String username, String email, String password, String firstName, String status, LocalDate createdAt, Set<UserPagePermission> userPagePermisisons, List<UserCommentRating> userCommentRating, List<Comment> comment, Set<Role> role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.status = status;
        this.createdAt = createdAt;
        this.userPagePermisisons = userPagePermisisons;
        this.userCommentRating = userCommentRating;
        this.comment = comment;
        this.role = role;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public Set<UserPagePermission> getUserPagePermisisons() {
        return userPagePermisisons;
    }

    public List<UserCommentRating> getUserCommentRating() {
        return userCommentRating;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public Set<Role> getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", userPagePermisisons=" + userPagePermisisons +
                ", userCommentRating=" + userCommentRating +
                ", comment=" + comment +
                ", role=" + role +
                '}';
    }
}
