package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PasswordChanges {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", referencedColumnName = "id")
    private User user;

    private String password;
    private LocalDateTime dateUpdated;

    public PasswordChanges() {
    }

    public PasswordChanges(long id, User user, String password, LocalDateTime dateUpdated) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.dateUpdated = dateUpdated;
    }

    public PasswordChanges(User user, String password){
        this.user = user;
        this.password = password;
        this.dateUpdated = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
