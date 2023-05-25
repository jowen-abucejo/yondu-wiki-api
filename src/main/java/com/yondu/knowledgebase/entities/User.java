package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDate;


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

    public User(Long id, String username, String email, String password, String firstName, String status, LocalDate createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.status = status;
        this.createdAt = createdAt;
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
                '}';
    }
}
