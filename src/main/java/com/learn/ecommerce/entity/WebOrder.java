package com.learn.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "web_order")

public class WebOrder extends BaseAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(mappedBy = "webOrder", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<OrderQuantities> orderQuantities = new ArrayList<>();

    public Collection<OrderQuantities> getOrderQuantities() {
        return orderQuantities;
    }

    public void setOrderQuantities(Collection<OrderQuantities> orderQuantities) {
        this.orderQuantities = orderQuantities;
    }



}