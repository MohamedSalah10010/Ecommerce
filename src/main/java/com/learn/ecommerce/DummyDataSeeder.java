package com.learn.ecommerce;

import com.learn.ecommerce.entity.*;
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
            OrderQuantitiesRepo orderQuantitiesRepo,
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
            // =====================================
            // ROLES (safe check to avoid duplicates)
            // =====================================
            UserRoles adminRole = rolesRepo.findByRoleName("ADMIN")
                    .orElseGet(() -> {
                        UserRoles r = new UserRoles();
                        r.setRoleName("ADMIN");
                        return rolesRepo.save(r);
                    });

            UserRoles userRole = rolesRepo.findByRoleName("USER")
                    .orElseGet(() -> {
                        UserRoles r = new UserRoles();
                        r.setRoleName("USER");
                        return rolesRepo.save(r);
                    });


            // ===========================
            // USERS
            // ===========================
            LocalUser u1 = LocalUser.builder()
                    .userName("elmorgel")
                    .firstName("mohamed")
                    .lastName("elmorgel")
                    .email("mohamedelmorgel2001@gmail.com")
                    .password(encryptionService.encryptPassword("123456"))
                    .phoneNumber("+201001112233")
                    .isEnabled(true)
                    .isVerified(true)
                    .userRoles(new ArrayList<>()) // initialize collection
                    .addresses(new ArrayList<>()) // also initialize addresses
                    .verificationTokens(new ArrayList<>()) // initialize tokens
                    .build();
            u1.getUserRoles().add(userRole);

// u2 using setters
            LocalUser u2 = new LocalUser();
            u2.setUserName("abosalah");
            u2.setFirstName("abdo");
            u2.setLastName("salah");
            u2.setEmail("abosalah100001000@gmail.com");
            u2.setPassword(encryptionService.encryptPassword("123456"));
            u2.setPhoneNumber("+201001112234");
            u2.setIsEnabled(true);
            u2.setIsVerified(true);
            u2.setUserRoles(new ArrayList<>());
            u2.setAddresses(new ArrayList<>());
            u2.setVerificationTokens(new ArrayList<>());
            u2.getUserRoles().add(userRole);

// u3 using setters
            LocalUser u3 = new LocalUser();
            u3.setUserName("ahmed10");
            u3.setFirstName("Ahmed");
            u3.setLastName("Salah");
            u3.setEmail("ahmedsalah772015@gmail.com");
            u3.setPassword(encryptionService.encryptPassword("123456"));
            u3.setPhoneNumber("+201001112235");
            u3.setIsEnabled(true);
            u3.setIsVerified(true);
            u3.setUserRoles(new ArrayList<>());
            u3.setAddresses(new ArrayList<>());
            u3.setVerificationTokens(new ArrayList<>());
            u3.getUserRoles().add(userRole);

// save users
            userRepo.save(u1);
            userRepo.save(u2);
            userRepo.save(u3);

            // ===========================
            // ADDRESSES
            // ===========================
            Address a1 = Address.builder()
                    .user(u1)
                    .addressLine1("12 Main Street")
                    .city("Cairo")
                    .country("Egypt")
                    .build();
            u1.getAddresses().add(a1);

            Address a2 = Address.builder()
                    .user(u2)
                    .addressLine1("45 Nile Road")
                    .city("Giza")
                    .country("Egypt")
                    .build();
            u2.getAddresses().add(a2);

            Address a3 = Address.builder()
                    .user(u3)
                    .addressLine1("88 Sunset Blvd")
                    .city("Alexandria")
                    .country("Egypt")
                    .build();
            u3.getAddresses().add(a3);

            addressRepo.save(a1);
            addressRepo.save(a2);
            addressRepo.save(a3);

            // ===========================
            // PRODUCTS & INVENTORY
            // ===========================

// Product 1
            Product p1 = Product.builder()
                    .name("Laptop Lenovo")
                    .shortDescription("14-inch notebook")
                    .longDescription("Lenovo IdeaPad with 8GB RAM and SSD")
                    .price(15000.0)
                    .build();
            Inventory inv1 = new Inventory();
            inv1.setProduct(p1);
            inv1.setQuantity(10);
            inventoryRepo.save(inv1); // save inventory first
            p1.setInventory(inv1);
            productRepo.save(p1); // then save product

// Product 2
            Product p2 = Product.builder()
                    .name("Wireless Mouse")
                    .shortDescription("Optical wireless mouse")
                    .longDescription("Ergonomic design, 1600 DPI")
                    .price(250.0)
                    .build();
            Inventory inv2 = new Inventory();
            inv2.setProduct(p2);
            inv2.setQuantity(50);
            inventoryRepo.save(inv2);
            p2.setInventory(inv2);
            productRepo.save(p2);

// Product 3
            Product p3 = Product.builder()
                    .name("Smartphone Samsung")
                    .shortDescription("Galaxy A55")
                    .longDescription("128GB storage, 8GB RAM")
                    .price(12000.0)
                    .build();
            Inventory inv3 = new Inventory();
            inv3.setProduct(p3);
            inv3.setQuantity(20);
            inventoryRepo.save(inv3);
            p3.setInventory(inv3);
            productRepo.save(p3);

// Product 4
            Product p4 = Product.builder()
                    .name("Keyboard Mechanical")
                    .shortDescription("Backlit keyboard")
                    .longDescription("Blue switches, RGB lighting")
                    .price(800.0)
                    .build();
            Inventory inv4 = new Inventory();
            inv4.setProduct(p4);
            inv4.setQuantity(30);
            inventoryRepo.save(inv4);
            p4.setInventory(inv4);
            productRepo.save(p4);

// Product 5
            Product p5 = Product.builder()
                    .name("USB-C Cable")
                    .shortDescription("Fast charging cable")
                    .longDescription("1m long, supports 60W charging")
                    .price(120.0)
                    .build();
            Inventory inv5 = new Inventory();
            inv5.setProduct(p5);
            inv5.setQuantity(100);
            inventoryRepo.save(inv5);
            p5.setInventory(inv5);
            productRepo.save(p5);

            // ===========================
            // ORDERS
            // ===========================
            WebOrder order1 = WebOrder.builder()
                    .user(u1)
                    .address(a1)
                    .build();

            WebOrder order2 = WebOrder.builder()
                    .user(u2)
                    .address(a2)
                    .build();

            orderRepo.save(order1);
            orderRepo.save(order2);

            // ===========================
            // ORDER QUANTITIES
            // ===========================
            order1.setOrderQuantities(new ArrayList<>());
            order2.setOrderQuantities(new ArrayList<>());

            OrderQuantities oq1 = OrderQuantities.builder()
                    .webOrder(order1)
                    .product(p1)
                    .quantity(1)
                    .build();
            order1.getOrderQuantities().add(oq1);

            OrderQuantities oq2 = OrderQuantities.builder()
                    .webOrder(order1)
                    .product(p2)
                    .quantity(2)
                    .build();
            order1.getOrderQuantities().add(oq2);

            OrderQuantities oq3 = OrderQuantities.builder()
                    .webOrder(order2)
                    .product(p3)
                    .quantity(1)
                    .build();
            order2.getOrderQuantities().add(oq3);

            orderQuantitiesRepo.save(oq1);
            orderQuantitiesRepo.save(oq2);
            orderQuantitiesRepo.save(oq3);

            System.out.println("🌱 Database seeded successfully with bidirectional links!");
        };
    }
}
