package com.learn.ecommerce.controller.cart;

import com.learn.ecommerce.DTO.Cart.CartDTO;
import com.learn.ecommerce.DTO.Cart.CartStatusDTO;
import com.learn.ecommerce.DTO.CartItem.AddItemDTO;
import com.learn.ecommerce.DTO.ErrorResponseDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.exceptionhandler.UserNotFoundException;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Getter
@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Manage user shopping cart")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;
    private final LocalUserRepo localUserRepo;

    public CartController(CartService cartService, LocalUserRepo localUserRepo) {
        this.cartService = cartService;
        this.localUserRepo = localUserRepo;
    }

    // ------------------------------------------------
    // Get current active cart
    // ------------------------------------------------
    @Operation(summary = "Get active cart", description = "Retrieve the current active cart for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/get")
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal User userDetails) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        log.info("Fetching active cart for user: {}", user.getUsername());
        return ResponseEntity.ok(cartService.getCurrentActiveCart(user));
    }

    // ------------------------------------------------
    // Create new cart
    // ------------------------------------------------
    @Operation(summary = "Create cart", description = "Create a new cart for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cart created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/create-cart")
    public ResponseEntity<CartDTO> createCart(@AuthenticationPrincipal User userDetails) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        log.info("Creating new cart for user: {}", user.getUsername());
        return new ResponseEntity<>(cartService.createNewCartForUser(user), HttpStatus.CREATED);
    }

    // ------------------------------------------------
    // Add item to cart
    // ------------------------------------------------
    @Operation(summary = "Add item", description = "Add a product item to the user's active cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User or Product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/add-item")
    public ResponseEntity<CartStatusDTO> addItemToCart(
            @AuthenticationPrincipal User userDetails,
            @Parameter(description = "Item to add") @RequestBody AddItemDTO body
    ) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        log.info("Adding item (productId={}, quantity={}) to cart for user: {}",
                body.getProductId(), body.getQuantity(), user.getUsername());

        return ResponseEntity.ok(cartService.addItemToCart(user, body));
    }

    // ------------------------------------------------
    // Delete item from cart
    // ------------------------------------------------
    @Operation(summary = "Delete item", description = "Remove a specific item from the user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User or Item not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<CartStatusDTO> deleteItemFromCart(
            @AuthenticationPrincipal User userDetails,
            @Parameter(description = "ID of the item to remove") @PathVariable Long itemId
    ) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        log.warn("Deleting item id={} from cart for user: {}", itemId, user.getUsername());
        return ResponseEntity.ok(cartService.deleteCartItem(user, itemId));
    }

    // ------------------------------------------------
    // Delete entire cart
    // ------------------------------------------------
    @Operation(summary = "Delete cart", description = "Remove the user's entire active cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @DeleteMapping("/delete-cart")
    public ResponseEntity<CartStatusDTO> deleteCart(@AuthenticationPrincipal User userDetails) {
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        log.warn("Deleting entire cart for user: {}", user.getUsername());
        return ResponseEntity.ok(cartService.deleteCart(user));
    }
}
