package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.Order.OrderDTO;
import com.learn.ecommerce.DTO.Order.OrderItemDTO;
import com.learn.ecommerce.entity.*;
import com.learn.ecommerce.exceptionhandler.InsufficientStockException;
import com.learn.ecommerce.exceptionhandler.ItemNotFoundException;
import com.learn.ecommerce.exceptionhandler.UserNotFoundException;
import com.learn.ecommerce.repository.*;
import com.learn.ecommerce.utils.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class OrderService {

    private final WebOrderRepo orderRepo;
    private final OrderItemsRepo orderItemRepo;
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final InventoryRepo inventoryRepo;
    private final ProductRepo productRepo;
    private final AddressRepo addressRepo;

    public OrderService(WebOrderRepo orderRepo,
                        OrderItemsRepo orderItemRepo,
                        CartRepo cartRepo,
                        CartItemRepo cartItemRepo,
                        InventoryRepo inventoryRepo,
                        ProductRepo productRepo,
                        AddressRepo addressRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
        this.addressRepo = addressRepo;
    }

    // -------------------------
    // Place order from cart
    // -------------------------
    @Transactional
    public OrderDTO placeOrderFromCart(LocalUser user, Long addressId) {

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException());

        // Get active cart
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), com.learn.ecommerce.utils.CartStatus.ACTIVE)
                .orElseThrow(() -> new AccessDeniedException("No active cart found"));

        if (cart.getItems().isEmpty()) {
            throw new ItemNotFoundException("Cart is empty");
        }

        WebOrder order = WebOrder.builder()
                .user(user)
                .address(address)
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(0.0)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.isDeleted()) continue;

            Inventory inventory = cartItem.getProduct().getInventory();

            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException();
            }

            // Deduct inventory
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventoryRepo.save(inventory);

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .webOrder(order)
                    .build();

            orderItems.add(orderItem);
            totalPrice += cartItem.getQuantity() * cartItem.getProduct().getPrice();
        }

        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);
        orderRepo.save(order);
        orderItemRepo.saveAll(orderItems);

        // Mark cart items as deleted
        cart.getItems().forEach(item -> item.setDeleted(true));
        cart.setDeleted(true);
        cartRepo.save(cart);

        return mapToDTO(order);
    }

    // -------------------------
    // Get orders for a user
    // -------------------------
    public List<OrderDTO> getOrdersForUser(LocalUser user) {
        List<WebOrder> orders = orderRepo.findByUserIdAndIsDeletedFalse(user.getId());
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // -------------------------
    // Get single order
    // -------------------------
    public OrderDTO getOrderById(LocalUser user, Long orderId) {
        WebOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot access this order");
        }

        return mapToDTO(order);
    }

    // -------------------------
    // Helper: map WebOrder -> DTO
    // -------------------------
    private OrderDTO mapToDTO(WebOrder order) {
        List<OrderItemDTO> items = order.getOrderItems().stream()
                .filter(item -> !item.isDeleted())
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getProduct().getPrice())
                        .totalPrice(item.getQuantity() * item.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus().name())
                .totalPrice(order.getTotalPrice())
                .items(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
