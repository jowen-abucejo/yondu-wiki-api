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

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonInclude(Include.NON_EMPTY)
    private String status;

    // @JsonInclude(Include.NON_EMPTY)
    // private Set<UserPagePermissionDTO> userPagePermissions = new HashSet<>();

    // @JsonInclude(Include.NON_EMPTY)
    // private List<UserCommentRatingDTO> userCommentRating = new ArrayList<>();

    // @JsonInclude(Include.NON_EMPTY)
    // private List<CommentDTO> comment = new ArrayList<>();

    // @JsonInclude(Include.NON_EMPTY)
    // private Set<UserDirectoryAccessDTO> userDirectoryAccesses;

    // @JsonInclude(Include.NON_EMPTY)
    // private Set<RoleDTO> role = new HashSet<>();

    @JsonInclude(Include.NON_EMPTY)
    private LocalDate createdAt;

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
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

        public UserDTOBuilder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserDTO build() {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(id);
            userDTO.setUsername(username);
            userDTO.setEmail(email);
            userDTO.setPassword(password);
            userDTO.setFirstName(firstName);
            userDTO.setLastName(lastName);
            userDTO.setStatus(status);
            userDTO.setCreatedAt(createdAt);
            return userDTO;
        }
    }
}
