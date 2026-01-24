package com.learn.ecommerce.controller.order;

import com.learn.ecommerce.DTO.ErrorResponseDTO;
import com.learn.ecommerce.DTO.Order.OrderDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Getter
@RestController
@RequestMapping("/orders")
@Tag(
        name = "Orders",
        description = "Place orders, view orders, and get order details"
)
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(
            summary = "Place order from active cart",
            description = "Creates a new order for the authenticated user's active cart using the specified address"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or invalid"),
            @ApiResponse(responseCode = "404", description = "User or address not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/place")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<@NotNull OrderDTO> placeOrder(
            @AuthenticationPrincipal LocalUser user,
            @RequestParam Long addressId
    ) {
        log.info("User {} attempting to place an order with addressId {}", user.getUsername(), addressId);

        OrderDTO orderDTO = orderService.placeOrderFromCart(user, addressId);
        log.info("Order placed successfully for user {}. Order ID: {}", user.getUsername(), orderDTO.getId());
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    // -----------------------------
    // Get all orders for current user
    // -----------------------------
    @Operation(
            summary = "Get all orders for authenticated user",
            description = "Returns a list of all orders placed by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/my-orders")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<@NotNull List<OrderDTO>> getUserOrders(@AuthenticationPrincipal LocalUser user) {
        log.info("Fetching all orders for user: {}", user.getUsername());

        List<OrderDTO> orders = orderService.getOrdersForUser(user);
        log.info("Retrieved {} orders for user: {}", orders.size(), user.getUsername());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // -----------------------------
    // Get single order by ID
    // -----------------------------
    @Operation(
            summary = "Get single order by ID",
            description = "Returns detailed information for a specific order belonging to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User or order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{orderId}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<@NotNull OrderDTO> getOrderById(
            @AuthenticationPrincipal LocalUser user,
            @PathVariable Long orderId
    ) {
        log.info("Fetching order ID {} for user: {}", orderId, user.getUsername());


        OrderDTO order = orderService.getOrderById(user, orderId);
        log.info("Order ID {} retrieved for user: {}", orderId, user.getUsername());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}