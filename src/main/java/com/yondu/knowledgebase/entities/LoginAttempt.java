package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private int attempts;
    private LocalDateTime lastAttempt;
    private boolean isRestricted;
    private LocalDateTime removeRestriction;

    public LoginAttempt() {}

    public LoginAttempt(Long id, User user, int attempts, LocalDateTime lastAttempt, boolean isRestricted, LocalDateTime removeRestriction) {
        this.id = id;
        this.user = user;
        this.attempts = attempts;
        this.lastAttempt = lastAttempt;
        this.isRestricted = isRestricted;
        this.removeRestriction = removeRestriction;
    }
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public int getAttempts() {return attempts;}
    public void setAttempts(int attempts) {this.attempts = attempts;}
    public LocalDateTime getLastAttempt() {return lastAttempt;}
    public void setLastAttempt(LocalDateTime lastAttempt) {this.lastAttempt = lastAttempt;}
    public boolean isRestricted() {return isRestricted;}
    public void setRestricted(boolean restricted) {isRestricted = restricted;}
    public LocalDateTime getRemoveRestriction() {return removeRestriction;}
    public void setRemoveRestriction(LocalDateTime removeRestriction) {this.removeRestriction = removeRestriction;}
}