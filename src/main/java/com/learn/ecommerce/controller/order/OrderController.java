package com.learn.ecommerce.controller.order;

import com.learn.ecommerce.DTO.Order.OrderDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.exceptionhandler.UserNotFoundException;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.OrderService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Getter
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final LocalUserRepo localUserRepo;

    public OrderController(OrderService orderService, LocalUserRepo localUserRepo) {
        this.orderService = orderService;
        this.localUserRepo = localUserRepo;
    }

    // -----------------------------
    // Place order from active cart
    // -----------------------------
    @PostMapping("/place")
    public ResponseEntity<OrderDTO> placeOrder(
            @AuthenticationPrincipal User userDetails,
            @RequestParam Long addressId
    ) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        OrderDTO orderDTO = orderService.placeOrderFromCart(user, addressId);
        log.info("Order placed successfully for user: {}", user.getUsername());
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    // -----------------------------
    // Get all orders for current user
    // -----------------------------
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@AuthenticationPrincipal User userDetails) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        List<OrderDTO> orders = orderService.getOrdersForUser(user);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // -----------------------------
    // Get single order by ID
    // -----------------------------
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(
            @AuthenticationPrincipal User userDetails,
            @PathVariable Long orderId
    ) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

      OrderDTO order = orderService.getOrderById(user, orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
