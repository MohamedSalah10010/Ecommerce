package com.learn.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_roles")
public class UserRoles extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="role", nullable = false,unique = true)
    private String roleName;
    @Column(name = "is_deleted", nullable = false,columnDefinition = "BIT DEFAULT 0")
    private boolean isDeleted=false;


}
