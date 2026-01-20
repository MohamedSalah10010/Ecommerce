package com.learn.ecommerce.entity;

import com.learn.ecommerce.utils.CartStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart extends BaseAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("is_deleted = false")
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isDeleted = false;


    @Enumerated(EnumType.STRING)
    private CartStatus status; // ACTIVE, CHECKED_OUT, CANCELLED

}