package com.learn.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "local_user")
public class LocalUser extends BaseAuditEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @JsonIgnore
    @Column(nullable = false, length = 1000)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true, length = 13)
    private String phoneNumber;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id DESC")
    @JsonIgnore
    private Collection<VerificationToken> verificationTokens = new ArrayList<>();

    /* ===============================
      ACCOUNT FLAGS
      =============================== */
    @Column(name = "is_enabled", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isEnabled = false;

    @Column(name = "is_verified", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isVerified = false;

    @Column(name = "is_locked", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isLocked = false;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isDeleted = false;

/* ===============================
       USER ROLES
       =============================== */

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "local_user_user_roles",
            joinColumns = @JoinColumn(name = "local_user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_role_id")
    )
    private Collection<UserRoles> userRoles = new ArrayList<>();

    /* ===============================
       ADDRESSES
       =============================== */

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Collection<Address> addresses = new ArrayList<>();

    /* ===============================
       CARTS
       =============================== */

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    /* ===============================
       UserDetails IMPLEMENTATION
       =============================== */

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.userName;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return this.isEnabled;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !this.isLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
