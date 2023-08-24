package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean isValid;

    // Other fields, getters, setters, and constructors
    public UserOtp() {}

    public UserOtp( User user, String otp, LocalDateTime createdAt, LocalDateTime expirationDate, Boolean isValid) {
        this.user = user;
        this.otp = otp;
        this.createdAt = createdAt;
        this.expirationDate = expirationDate;
        this.isValid = isValid;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public String getOtp() {return otp;}
    public void setOtp(String otp) {this.otp = otp;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public LocalDateTime getExpirationDate() {return expirationDate;}
    public void setExpirationDate(LocalDateTime expirationDate) {this.expirationDate = expirationDate;}
    public Boolean getValid() {return isValid;}
    public void setValid(Boolean valid) {isValid = valid;}
}
