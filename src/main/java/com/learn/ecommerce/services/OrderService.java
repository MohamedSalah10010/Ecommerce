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
        log.info("Placing order for user={} with addressId={}", user.getUsername(), addressId);

        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> {
                    log.warn("Address not found for addressId={}", addressId);
                    return new UserNotFoundException();
                });

        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), com.learn.ecommerce.utils.CartStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("No active cart found for user={}", user.getUsername());
                    return new AccessDeniedException("No active cart found");
                });

        if (cart.getItems().isEmpty()) {
            log.warn("Cart is empty for user={}", user.getUsername());
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
                log.warn("Insufficient stock for product={} requested={} available={}",
                        cartItem.getProduct().getName(), cartItem.getQuantity(), inventory.getQuantity());
                throw new InsufficientStockException();
            }

            // Deduct inventory
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventoryRepo.save(inventory);

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

        cart.getItems().forEach(item -> item.setDeleted(true));
        cart.setDeleted(true);
        cartRepo.save(cart);

        log.info("Order placed successfully for user={} orderId={} totalPrice={}",
                user.getUsername(), order.getId(), totalPrice);

        return mapToDTO(order);
    }

    // -------------------------
    // Get orders for a user
    // -------------------------
    public List<OrderDTO> getOrdersForUser(LocalUser user) {
        log.info("Fetching orders for user={}", user.getUsername());

        List<WebOrder> orders = orderRepo.findByUserIdAndIsDeletedFalse(user.getId());
        log.info("Found {} orders for user={}", orders.size(), user.getUsername());

        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // -------------------------
    // Get single order
    // -------------------------
    public OrderDTO getOrderById(LocalUser user, Long orderId) {
        log.info("Fetching orderId={} for user={}", orderId, user.getUsername());

        WebOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found: orderId={}", orderId);
                    return new ItemNotFoundException("Order not found");
                });

        if (!order.getUser().getId().equals(user.getId())) {
            log.warn("Access denied: user={} tried to access orderId={}", user.getUsername(), orderId);
            throw new AccessDeniedException("You cannot access this order");
        }

        log.info("Order fetched successfully: orderId={} for user={}", orderId, user.getUsername());
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
