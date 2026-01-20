package com.learn.ecommerce;

import com.learn.ecommerce.entity.*;
import com.learn.ecommerce.repository.*;
import com.learn.ecommerce.services.EncryptionService;
import com.learn.ecommerce.utils.CartStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class DummyDataSeeder {

    private final EncryptionService encryptionService;

    public DummyDataSeeder(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Bean
    CommandLineRunner dataSeeder(
            LocalUserRepo userRepo,
            AddressRepo addressRepo,
            ProductRepo productRepo,
            InventoryRepo inventoryRepo,
            CartRepo cartRepo,
            CartItemRepo cartItemRepo,
            WebOrderRepo orderRepo,
            OrderItemsRepo orderItemsRepo,
            UserRolesRepo rolesRepo
    ) {
        return args -> {

            if (userRepo.count() > 0) {
                System.out.println("Seed skipped — data already exists.");
                return;
            }

            // ===========================
            // ROLES
            // ===========================
            UserRoles adminRole = rolesRepo.findByRoleName("ADMIN")
                    .orElseGet(() -> rolesRepo.save(UserRoles.builder().roleName("ADMIN").build()));
            UserRoles userRole = rolesRepo.findByRoleName("USER")
                    .orElseGet(() -> rolesRepo.save(UserRoles.builder().roleName("USER").build()));

            // ===========================
            // USERS
            // ===========================
            LocalUser u1 = LocalUser.builder()
                    .userName("elmorgel")
                    .firstName("Mohamed")
                    .lastName("Elmorgel")
                    .email("mohamedelmorgel2001@gmail.com")
                    .password(encryptionService.encryptPassword("123456"))
                    .phoneNumber("+201001112233")
                    .isEnabled(true)
                    .isVerified(true)
                    .userRoles(userRole.getRoleName().equals("ADMIN") ?
                            new ArrayList<>() {{
                                add(adminRole);
                                add(userRole);
                            }} :
                            new ArrayList<>())
                    .addresses(new ArrayList<>())
                    .verificationTokens(new ArrayList<>())
                    .build();
            u1.getUserRoles().add(userRole);

            LocalUser u2 = LocalUser.builder()
                    .userName("abosalah")
                    .firstName("Abdo")
                    .lastName("Salah")
                    .email("abosalah100001000@gmail.com")
                    .password(encryptionService.encryptPassword("123456"))
                    .phoneNumber("+201001112234")
                    .isEnabled(true)
                    .isVerified(true)
                    .userRoles(new ArrayList<>())
                    .addresses(new ArrayList<>())
                    .verificationTokens(new ArrayList<>())
                    .build();
            u2.getUserRoles().add(userRole);

            LocalUser u3 = LocalUser.builder()
                    .userName("ahmed10")
                    .firstName("Ahmed")
                    .lastName("Salah")
                    .email("ahmedsalah772015@gmail.com")
                    .password(encryptionService.encryptPassword("123456"))
                    .phoneNumber("+201001112235")
                    .isEnabled(true)
                    .isVerified(true)
                    .userRoles(new ArrayList<>())
                    .addresses(new ArrayList<>())
                    .verificationTokens(new ArrayList<>())
                    .build();
            u3.getUserRoles().add(userRole);

            userRepo.save(u1);
            userRepo.save(u2);
            userRepo.save(u3);

            // ===========================
            // ADDRESSES
            // ===========================
            Address a1 = Address.builder().user(u1).addressLine1("12 Main Street").city("Cairo").country("Egypt").build();
            Address a2 = Address.builder().user(u2).addressLine1("45 Nile Road").city("Giza").country("Egypt").build();
            Address a3 = Address.builder().user(u3).addressLine1("88 Sunset Blvd").city("Alexandria").country("Egypt").build();
            u1.getAddresses().add(a1);
            u2.getAddresses().add(a2);
            u3.getAddresses().add(a3);
            addressRepo.save(a1);
            addressRepo.save(a2);
            addressRepo.save(a3);

            // ===========================
            // PRODUCTS & INVENTORY
            // ===========================
            Product p1 = Product.builder().name("Laptop Lenovo").shortDescription("14-inch notebook")
                    .longDescription("Lenovo IdeaPad with 8GB RAM and SSD").price(15000.0).build();
            Product p2 = Product.builder().name("Wireless Mouse").shortDescription("Optical wireless mouse")
                    .longDescription("Ergonomic design, 1600 DPI").price(250.0).build();
            Product p3 = Product.builder().name("Smartphone Samsung").shortDescription("Galaxy A55")
                    .longDescription("128GB storage, 8GB RAM").price(12000.0).build();
            Product p4 = Product.builder().name("Keyboard Mechanical").shortDescription("Backlit keyboard")
                    .longDescription("Blue switches, RGB lighting").price(800.0).build();
            Product p5 = Product.builder().name("USB-C Cable").shortDescription("Fast charging cable")
                    .longDescription("1m long, supports 60W charging").price(120.0).build();

            Inventory inv1 = Inventory.builder().product(p1).quantity(10).build();
            Inventory inv2 = Inventory.builder().product(p2).quantity(50).build();
            Inventory inv3 = Inventory.builder().product(p3).quantity(20).build();
            Inventory inv4 = Inventory.builder().product(p4).quantity(30).build();
            Inventory inv5 = Inventory.builder().product(p5).quantity(100).build();

            inventoryRepo.save(inv1);
            inventoryRepo.save(inv2);
            inventoryRepo.save(inv3);
            inventoryRepo.save(inv4);
            inventoryRepo.save(inv5);

            p1.setInventory(inv1);
            p2.setInventory(inv2);
            p3.setInventory(inv3);
            p4.setInventory(inv4);
            p5.setInventory(inv5);

            productRepo.save(p1);
            productRepo.save(p2);
            productRepo.save(p3);
            productRepo.save(p4);
            productRepo.save(p5);

            // ===========================
            // CARTS & CART ITEMS
            // ===========================
            Cart cart1 = Cart.builder().user(u1).status(CartStatus.ACTIVE).isDeleted(false).items(new ArrayList<>()).build();
            Cart cart2 = Cart.builder().user(u2).status(CartStatus.ACTIVE).isDeleted(false).items(new ArrayList<>()).build();

            cartRepo.save(cart1);
            cartRepo.save(cart2);

            CartItem ci1 = CartItem.builder().cart(cart1).product(p1).quantity(1).priceAtAddition(p1.getPrice()).isDeleted(false).build();
            CartItem ci2 = CartItem.builder().cart(cart1).product(p2).quantity(2).priceAtAddition(p2.getPrice()).isDeleted(false).build();
            CartItem ci3 = CartItem.builder().cart(cart2).product(p3).quantity(1).priceAtAddition(p3.getPrice()).isDeleted(false).build();

            cartItemRepo.save(ci1);
            cartItemRepo.save(ci2);
            cartItemRepo.save(ci3);

            cart1.getItems().add(ci1);
            cart1.getItems().add(ci2);
            cart2.getItems().add(ci3);

            cartRepo.save(cart1);
            cartRepo.save(cart2);

            // ===========================
            // ORDERS & ORDER ITEMS
            // ===========================
            WebOrder order1 = WebOrder.builder().user(u1).address(a1).orderStatus(com.learn.ecommerce.utils.OrderStatus.PENDING).totalPrice(0.0).orderItems(new ArrayList<>()).build();
            WebOrder order2 = WebOrder.builder().user(u2).address(a2).orderStatus(com.learn.ecommerce.utils.OrderStatus.PENDING).totalPrice(0.0).orderItems(new ArrayList<>()).build();

            orderRepo.save(order1);
            orderRepo.save(order2);

            OrderItem oi1 = OrderItem.builder().webOrder(order1).product(p1).quantity(1).isDeleted(false).build();
            OrderItem oi2 = OrderItem.builder().webOrder(order1).product(p2).quantity(2).isDeleted(false).build();
            OrderItem oi3 = OrderItem.builder().webOrder(order2).product(p3).quantity(1).isDeleted(false).build();

            orderItemsRepo.save(oi1);
            orderItemsRepo.save(oi2);
            orderItemsRepo.save(oi3);

            order1.getOrderItems().add(oi1);
            order1.getOrderItems().add(oi2);
            order2.getOrderItems().add(oi3);

            orderRepo.save(order1);
            orderRepo.save(order2);

            System.out.println("🌱 Full database seeded successfully!");
        };
    }
}
