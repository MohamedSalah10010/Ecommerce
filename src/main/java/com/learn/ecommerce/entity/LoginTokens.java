package com.learn.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "login_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginTokens extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @Lob
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expired", nullable = false)
    private Boolean expired = false;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiresAt;

	@Column(name = "is_deleted", nullable = false, columnDefinition = "BIT DEFAULT 0")
	private boolean isDeleted = false;
}