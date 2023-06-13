package com.yondu.knowledgebase.entities;

import com.yondu.knowledgebase.DTO.user.UserDTO;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String status;
    private String profilePhoto;
    private String position;
    private LocalDate createdAt;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<PageRights> pageRights = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommentRating> userCommentRating = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comment = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> role = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Directory> createdDirectories;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<Notification> notifications = new HashSet<>();

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
//    private Set<Group> group = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_rights", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "rights_id"))
    private Set<Rights> rights; // To Delete

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserPageAccess> userPageAccess = new HashSet<>();

    public User(long id) {
        this.id = id;
    }

    public User(UserDTO.GeneralInfo user) {
        this.id = user.id();
        this.username = user.username();
        this.email = user.email();
        this.password = user.password();
        this.firstName = user.firstName();
        this.lastName = user.lastName();
        this.status = user.status();
        this.createdAt = user.createdAt();
        this.rights = new HashSet<>();
    }

    public User(UserDTO.WithRolesRequest user) {
        this.id = user.id();
        this.username = user.username();
        this.email = user.email();
        this.password = user.password();
        this.firstName = user.firstName();
        this.lastName = user.lastName();
        this.profilePhoto = user.profilePhoto();
        this.position = user.position();
        this.status = user.status();
        this.createdAt = user.createdAt();
        this.rights = new HashSet<>();

        Set<Role> roles = user.roles()
                .stream()
                .map(role -> new Role(role.getId()))
                .collect(Collectors.toSet());
        this.role = roles;
    }

    public User(Long id, String username, String email, String password, String firstName, String lastName,
            String status, LocalDate createdAt, Set<Role> role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.createdAt = createdAt;
        this.role = role;
        this.rights = new HashSet<>(); // To delete
        this.userPageAccess = new HashSet<>();
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

    public String getLastName() {
        return lastName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Set<Role> getRole() {
        return role;
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public Set<Rights> getRights() {
        return rights;
    } // to delete

    public void setRights(Set<Rights> rights) {
        this.rights = rights;
    } // To delete

    public Set<UserPageAccess> getUserPageAccess() {
        return userPageAccess;
    }

    public void setUserPageAccess(Set<UserPageAccess> userPageAccess) {
        this.userPageAccess = userPageAccess;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        // Compare fields for equality
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", role=" + role +
                '}';
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Set<Role> roles = getRole();
        roles.stream()
                        .forEach(role ->
                                role.getUserPermissions()
                                        .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())))
                        );
        return authorities;
//        throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return status.equals("ACT");
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return status.equals("ACT");
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return status.equals("ACT");
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return status.equals("ACT");
    }

}
