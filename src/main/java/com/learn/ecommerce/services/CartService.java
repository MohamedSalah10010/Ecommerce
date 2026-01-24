package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.Cart.CartDTO;
import com.learn.ecommerce.DTO.Cart.CartStatusDTO;
import com.learn.ecommerce.DTO.Cart.UpdateQuantityDTO;
import com.learn.ecommerce.DTO.CartItem.AddItemDTO;
import com.learn.ecommerce.DTO.CartItem.ItemDTO;
import com.learn.ecommerce.entity.*;
import com.learn.ecommerce.enums.CartStatus;
import com.learn.ecommerce.exceptionhandler.CartIsEmptyException;
import com.learn.ecommerce.exceptionhandler.InsufficientStockException;
import com.learn.ecommerce.exceptionhandler.ItemNotFoundException;
import com.learn.ecommerce.exceptionhandler.ProductNotFoundException;
import com.learn.ecommerce.repository.CartItemRepo;
import com.learn.ecommerce.repository.CartRepo;
import com.learn.ecommerce.repository.InventoryRepo;
import com.learn.ecommerce.repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Service
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;
    private final InventoryRepo inventoryRepo;

    public CartService(CartRepo cartRepo, CartItemRepo cartItemRepo, ProductRepo productRepo, InventoryRepo inventoryRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
    }

    @Transactional
    public CartDTO getCurrentActiveCart(LocalUser user) {
        Optional<Cart> cartOptional = cartRepo.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            if (cart.isDeleted()) {
                log.info("Active cart for user {} is deleted. Creating a new cart.", user.getId());
                Cart newCart = createNewCart(user);
                return buildCartDTO(newCart, 0);
            }

            List<ItemDTO> itemListDTO = mappingListItemDTO(cart);
            double totalPrice = itemListDTO.stream().mapToDouble(ItemDTO::getTotalPrice).sum();
            log.info("Fetched active cart for user {} with {} items", user.getId(), itemListDTO.size());
            return buildCartDTO(cart, totalPrice, itemListDTO);
        }

        log.info("No active cart found for user {}. Creating a new cart.", user.getId());
        Cart newCart = createNewCart(user);
        return buildCartDTO(newCart, 0);
    }

    private CartDTO buildCartDTO(Cart cart, double totalPrice, List<ItemDTO> items) {
        return CartDTO.builder()
                .id(cart.getId())
                .status(cart.getStatus())
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    private CartDTO buildCartDTO(Cart cart, double totalPrice) {
        return buildCartDTO(cart, totalPrice, new ArrayList<>());
    }

    private Cart createNewCart(LocalUser user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setStatus(CartStatus.ACTIVE);
        newCart.setDeleted(false);
        cartRepo.save(newCart);
        log.info("Created new cart for user {} with id {}", user.getId(), newCart.getId());
        return newCart;
    }

    public CartDTO createNewCartForUser(LocalUser user) {
        Cart newCart = createNewCart(user);
        return buildCartDTO(newCart, 0);
    }

    private List<ItemDTO> mappingListItemDTO(Cart cart) {
        List<ItemDTO> itemListDTO = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            if (item.isDeleted()) continue;
            itemListDTO.add(ItemDTO.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getPriceAtAddition())
                    .totalPrice(item.getPriceAtAddition() * item.getQuantity())
                    .build());
        }
        return itemListDTO;
    }

    @Transactional
    public CartStatusDTO addItemToCart(LocalUser user, AddItemDTO dto) {
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseGet(() -> createNewCart(user));

        Product product = productRepo.findById(dto.getProductId())
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> {
                    log.warn("Product {} not found or deleted", dto.getProductId());
                    return new ProductNotFoundException("product not found");
                });

        CartItem item = cartItemRepo.findByCartAndProduct(cart, product)
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .priceAtAddition(product.getPrice())
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + dto.getQuantity());
        cartItemRepo.save(item);

        log.info("Added product {} (quantity: {}) to cart {} for user {}", product.getId(), dto.getQuantity(), cart.getId(), user.getId());
        return CartStatusDTO.builder()
                .id(cart.getId())
                .items(mappingListItemDTO(cart))
                .statusMessage("Item added successfully")
                .build();
    }

    @Transactional
    public CartDTO checkoutCart(LocalUser user, Long cartId) {
        Cart cart = cartRepo.findByIdAndUserId(user.getId(), cartId)
                .orElseThrow(() -> {
                    log.warn("No cart {} found for checkout for user {}", cartId, user.getId());
                    return new CartIsEmptyException("Cart items is empty, can't checkout cart");
                });

        if (cart.getStatus() != CartStatus.ACTIVE) {
            log.warn("Cart {} is not ACTIVE, can't checkout", cartId);
            throw new IllegalStateException("Cart status is not ACTIVE, can't checkout cart");
        }

        for (CartItem item : cart.getItems()) {
            if (item.isDeleted()) continue;
            Inventory inventory = item.getProduct().getInventory();
            if (inventory.getQuantity() < item.getQuantity()) {
                log.warn("Insufficient stock for product {} in cart {}", item.getProduct().getId(), cart.getId());
                throw new InsufficientStockException("Insufficient stock for product ");
            }
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventoryRepo.save(inventory);
        }

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepo.save(cart);
        List<ItemDTO> itemListDTO = mappingListItemDTO(cart);
        double totalPrice = itemListDTO.stream().mapToDouble(ItemDTO::getTotalPrice).sum();
        log.info("Checked out cart {} for user {} with {} items", cart.getId(), user.getId(), itemListDTO.size());
        return buildCartDTO(cart, totalPrice, itemListDTO);
    }

    @Transactional
    public CartStatusDTO updateCartItemQuantity(LocalUser user, Long itemId, UpdateQuantityDTO updateQuantityDTO) {
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("No active cart found for user {}", user.getId());
                    return new AccessDeniedException("No active cart");
                });

        CartItem cartItem = cartItemRepo.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> {
                    log.warn("Cart item {} not found in cart {}", itemId, cart.getId());
                    return new ItemNotFoundException("Item not found");
                });

        if (updateQuantityDTO.getQuantity() == 0) {
            log.info("Quantity 0 provided, deleting cart item {} from cart {}", itemId, cart.getId());
            return deleteCartItem(user, itemId);
        }

        cartItem.setQuantity(updateQuantityDTO.getQuantity());
        cartItemRepo.save(cartItem);

        log.info("Updated quantity for cart item {} to {} in cart {}", itemId, updateQuantityDTO.getQuantity(), cart.getId());
        return CartStatusDTO.builder()
                .statusMessage("Item updated successfully")
                .items(mappingListItemDTO(cart))
                .build();
    }

    @Transactional
    public CartStatusDTO deleteCartItem(LocalUser user, Long cartItemId) {
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("No active cart found for user {}", user.getId());
                    return new AccessDeniedException("No active cart");
                });

        CartItem cartItem = cartItemRepo.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> {
                    log.warn("Cart item {} not found in cart {}", cartItemId, cart.getId());
                    return new ItemNotFoundException("Item not found");
                });

        cartItem.setDeleted(true);
        cart.getItems().remove(cartItem);
        cartRepo.save(cartItem.getCart());
        cartItemRepo.save(cartItem);

        log.info("Deleted cart item {} from cart {}", cartItemId, cart.getId());
        return CartStatusDTO.builder().statusMessage("Item deleted successfully").build();
    }

    @Transactional
    public CartStatusDTO deleteCart(LocalUser user) {
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("No active cart found for user {}", user.getId());
                    return new AccessDeniedException("No active cart");
                });

        cart.setDeleted(true);
        cart.getItems().forEach(item -> item.setDeleted(true));
        cartRepo.save(cart);

        log.info("Deleted entire cart {} for user {}", cart.getId(), user.getId());
        return CartStatusDTO.builder().statusMessage("Cart deleted successfully").build();
    }
}