package com.learn.ecommerce;

import com.learn.ecommerce.model.*;
import com.learn.ecommerce.repository.*;
import com.learn.ecommerce.services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class dummyDataSeeder {

    private EncryptionService  encryptionService;

    public dummyDataSeeder(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Bean
    CommandLineRunner dataSeeder(
            LocalUserRepo userRepo,
            AddressRepo addressRepo,
            ProductRepo productRepo,
            InventoryRepo inventoryRepo,
            WebOrderRepo orderRepo,
            OrderQuantitiesRepo orderQuantitiesRepo
    ) {

        return args -> {

            // 🔒 Prevent duplicate seeding
            if (userRepo.count() > 0) {
                System.out.println("Seed skipped — data already exists.");
                return;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // =====================================
            // USERS
            // =====================================

            LocalUser u1 = new LocalUser();
            u1.setUserName("john123");
            u1.setFirstName("John");
            u1.setLastName("Smith");
            u1.setEmail("john@example.com");
            u1.setPassword(encryptionService.encryptPassword("123456"));
            userRepo.save(u1);

            LocalUser u2 = new LocalUser();
            u2.setUserName("sara99");
            u2.setFirstName("Sara");
            u2.setLastName("Adams");
            u2.setEmail("sara@example.com");
            u2.setPassword(encryptionService.encryptPassword("123456"));
            userRepo.save(u2);

            LocalUser u3 = new LocalUser();
            u3.setUserName("mohamed10");
            u3.setFirstName("Mohamed");
            u3.setLastName("Salah");
            u3.setEmail("mohamed@example.com");
            u3.setPassword(encryptionService.encryptPassword("123456"));
            userRepo.save(u3);

            // =====================================
            // ADDRESSES
            // =====================================

            Address a1 = new Address();
            a1.setUser(u1);
            a1.setAddressLine1("12 Main Street");
            a1.setCity("Cairo");
            a1.setCountry("Egypt");
            addressRepo.save(a1);

            Address a2 = new Address();
            a2.setUser(u2);
            a2.setAddressLine1("45 Nile Road");
            a2.setCity("Giza");
            a2.setCountry("Egypt");
            addressRepo.save(a2);

            Address a3 = new Address();
            a3.setUser(u3);
            a3.setAddressLine1("88 Sunset Blvd");
            a3.setCity("Alexandria");
            a3.setCountry("Egypt");
            addressRepo.save(a3);

            // =====================================
            // PRODUCTS + INVENTORY
            // =====================================

            Product p1 = new Product();
            p1.setName("Laptop Lenovo");
            p1.setShortDescription("14-inch notebook");
            p1.setLongDescription("Lenovo IdeaPad with 8GB RAM and SSD");
            p1.setPrice(15000.0);
            productRepo.save(p1);

            Inventory inv1 = new Inventory();
            inv1.setProduct(p1);
            inv1.setQuantity(10);
            inventoryRepo.save(inv1);

            Product p2 = new Product();
            p2.setName("Wireless Mouse");
            p2.setShortDescription("Optical wireless mouse");
            p2.setLongDescription("Ergonomic design, 1600 DPI");
            p2.setPrice(250.0);
            productRepo.save(p2);

            Inventory inv2 = new Inventory();
            inv2.setProduct(p2);
            inv2.setQuantity(50);
            inventoryRepo.save(inv2);

            Product p3 = new Product();
            p3.setName("Smartphone Samsung");
            p3.setShortDescription("Galaxy A55");
            p3.setLongDescription("128GB storage, 8GB RAM");
            p3.setPrice(12000.0);
            productRepo.save(p3);

            Inventory inv3 = new Inventory();
            inv3.setProduct(p3);
            inv3.setQuantity(20);
            inventoryRepo.save(inv3);

            Product p4 = new Product();
            p4.setName("Keyboard Mechanical");
            p4.setShortDescription("Backlit keyboard");
            p4.setLongDescription("Blue switches, RGB lighting");
            p4.setPrice(800.0);
            productRepo.save(p4);

            Inventory inv4 = new Inventory();
            inv4.setProduct(p4);
            inv4.setQuantity(30);
            inventoryRepo.save(inv4);

            Product p5 = new Product();
            p5.setName("USB-C Cable");
            p5.setShortDescription("Fast charging cable");
            p5.setLongDescription("1m long, supports 60W charging");
            p5.setPrice(120.0);
            productRepo.save(p5);

            Inventory inv5 = new Inventory();
            inv5.setProduct(p5);
            inv5.setQuantity(100);
            inventoryRepo.save(inv5);

            // =====================================
            // ORDERS
            // =====================================

            WebOrder order1 = new WebOrder();
            order1.setLocalUser(u1); // note: your setter is setLocalUser
            order1.setAddress(a1);
            orderRepo.save(order1);

            WebOrder order2 = new WebOrder();
            order2.setLocalUser(u2);
            order2.setAddress(a2);
            orderRepo.save(order2);

            // =====================================
            // ORDER QUANTITIES
            // =====================================

            OrderQuantities oq1 = new OrderQuantities();
            oq1.setWebOrder(order1);
            oq1.setProduct(p1);
            oq1.setQuantity(1);
            orderQuantitiesRepo.save(oq1);

            OrderQuantities oq2 = new OrderQuantities();
            oq2.setWebOrder(order1);
            oq2.setProduct(p2);
            oq2.setQuantity(2);
            orderQuantitiesRepo.save(oq2);

            OrderQuantities oq3 = new OrderQuantities();
            oq3.setWebOrder(order2);
            oq3.setProduct(p3);
            oq3.setQuantity(1);
            orderQuantitiesRepo.save(oq3);

            System.out.println("🌱 Database seeded successfully!");
        };
    }
}



