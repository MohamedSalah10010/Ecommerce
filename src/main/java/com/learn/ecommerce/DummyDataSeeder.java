package com.learn.ecommerce;

import com.learn.ecommerce.entity.*;
import com.learn.ecommerce.enums.OrderStatus;
import com.learn.ecommerce.repository.*;
import com.learn.ecommerce.services.EncryptionService;
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
            WebOrderRepo orderRepo,
            OrderItemsRepo orderItemRepo,
            UserRolesRepo rolesRepo
    ) {
        return args -> {

            if (userRepo.count() > 0) {
                System.out.println("Seed skipped — data already exists.");
                return;
            }

            // ===========================
            // USER ROLES
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
                    .userRoles(new ArrayList<>())
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
            u1.getAddresses().add(a1);
            Address a2 = Address.builder().user(u2).addressLine1("45 Nile Road").city("Giza").country("Egypt").build();
            u2.getAddresses().add(a2);
            Address a3 = Address.builder().user(u3).addressLine1("88 Sunset Blvd").city("Alexandria").country("Egypt").build();
            u3.getAddresses().add(a3);

            addressRepo.save(a1);
            addressRepo.save(a2);
            addressRepo.save(a3);

            // ===========================
            // PRODUCTS & INVENTORY
            // ===========================
            Product p1 = Product.builder().name("Laptop Lenovo").shortDescription("14-inch notebook")
                    .longDescription("Lenovo IdeaPad with 8GB RAM and SSD").price(15000.0).build();
            Inventory inv1 = new Inventory();
            inv1.setProduct(p1);
            inv1.setQuantity(10);
            inventoryRepo.save(inv1);
            p1.setInventory(inv1);
            productRepo.save(p1);

            Product p2 = Product.builder().name("Wireless Mouse").shortDescription("Optical wireless mouse")
                    .longDescription("Ergonomic design, 1600 DPI").price(250.0).build();
            Inventory inv2 = new Inventory();
            inv2.setProduct(p2);
            inv2.setQuantity(50);
            inventoryRepo.save(inv2);
            p2.setInventory(inv2);
            productRepo.save(p2);

            Product p3 = Product.builder().name("Smartphone Samsung").shortDescription("Galaxy A55")
                    .longDescription("128GB storage, 8GB RAM").price(12000.0).build();
            Inventory inv3 = new Inventory();
            inv3.setProduct(p3);
            inv3.setQuantity(20);
            inventoryRepo.save(inv3);
            p3.setInventory(inv3);
            productRepo.save(p3);

            Product p4 = Product.builder().name("Keyboard Mechanical").shortDescription("Backlit keyboard")
                    .longDescription("Blue switches, RGB lighting").price(800.0).build();
            Inventory inv4 = new Inventory();
            inv4.setProduct(p4);
            inv4.setQuantity(30);
            inventoryRepo.save(inv4);
            p4.setInventory(inv4);
            productRepo.save(p4);

            Product p5 = Product.builder().name("USB-C Cable").shortDescription("Fast charging cable")
                    .longDescription("1m long, supports 60W charging").price(120.0).build();
            Inventory inv5 = new Inventory();
            inv5.setProduct(p5);
            inv5.setQuantity(100);
            inventoryRepo.save(inv5);
            p5.setInventory(inv5);
            productRepo.save(p5);

            // ===========================
            // ORDERS
            // ===========================
            WebOrder order1 = WebOrder.builder().user(u1).address(a1).orderItems(new ArrayList<>()).build();
            WebOrder order2 = WebOrder.builder().user(u2).address(a2).orderItems(new ArrayList<>()).build();
            order1.setOrderStatus(OrderStatus.CREATED);
            order2.setOrderStatus(OrderStatus.CREATED);
            orderRepo.save(order1);
            orderRepo.save(order2);

            // ===========================
            // ORDER ITEMS
            // ===========================
            OrderItem oi1 = OrderItem.builder().webOrder(order1).product(p1).quantity(1).build();
            OrderItem oi2 = OrderItem.builder().webOrder(order1).product(p2).quantity(2).build();
            OrderItem oi3 = OrderItem.builder().webOrder(order2).product(p3).quantity(1).build();

            orderItemRepo.save(oi1);
            orderItemRepo.save(oi2);
            orderItemRepo.save(oi3);

            order1.getOrderItems().add(oi1);
            order1.getOrderItems().add(oi2);
            order2.getOrderItems().add(oi3);

            System.out.println("🌱 Database seeded successfully with new entity naming!");
        };
    }
}
