package com.learn.ecommerce.entity;

import com.learn.ecommerce.utils.OrderStatus;
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
@Table(name = "orders") // Avoid SQL Server reserved word "ORDER"
public class WebOrder extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

//    @Setter(AccessLevel.NONE)
    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "webOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<OrderItem> orderItems = new ArrayList<>();


    /* ------------------ Helper Methods ------------------ */

    public void addOrderItem(OrderItem item) {
        if (!orderItems.contains(item)) {
            orderItems.add(item);
            item.setWebOrder(this);
            recalculateTotal();
        }
    }

    public void removeOrderItem(OrderItem item) {
        if (orderItems.contains(item)) {
            orderItems.remove(item);
            item.setWebOrder(null);
            recalculateTotal();
        }
    }

    public void recalculateTotal() {
        totalPrice = orderItems.stream()
                .filter(i -> !i.isDeleted())
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
    }

    public void markDeleted() {
        this.isDeleted = true;
        orderItems.forEach(item -> item.setDeleted(true));
    }
}
