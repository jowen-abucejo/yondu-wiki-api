package com.yondu.knowledgebase.DTO.page;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class UserDTO {
    @JsonInclude(Include.NON_EMPTY)
    private Long id;

    @JsonInclude(Include.NON_EMPTY)
    private String username;

    @JsonInclude(Include.NON_EMPTY)
    private String email;

    @JsonInclude(Include.NON_EMPTY)
    private String password;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "profile_photo")
    private String profilePhoto;

    @JsonProperty(value = "position")
    private String position;

    @JsonInclude(Include.NON_EMPTY)
    private String status;

    @JsonInclude(Include.NON_EMPTY)
    private LocalDate createdAt;

    private UserDTO(UserDTOBuilder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.profilePhoto = builder.profilePhoto;
        this.position = builder.position;
        this.status = builder.status;
        this.createdAt = builder.createdAt;

    }

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    // Builder class
    public static class UserDTOBuilder {
        private Long id;
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String status;
        private String profilePhoto;
        private String position;
        private LocalDate createdAt;

        public UserDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserDTOBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDTOBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserDTOBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDTOBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDTOBuilder status(String status) {
            this.status = status;
            return this;
        }

        public UserDTOBuilder profilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
            return this;
        }

        public UserDTOBuilder position(String position) {
            this.position = position;
            return this;
        }

        public UserDTOBuilder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(this);
        }
    }
}
