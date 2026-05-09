package com.demo;

import com.demo.model.Product;
import com.demo.model.User;
import com.demo.repository.ProductRepository;
import com.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(UserRepository userRepo, ProductRepository productRepo) {
        return args -> {
            if (userRepo.count() == 0) {
                userRepo.save(new User("Alice Admin",  "alice@demo.com",  "ADMIN", "+91-9000000001"));
                userRepo.save(new User("Bob Builder",  "bob@demo.com",    "USER",  "+91-9000000002"));
                userRepo.save(new User("Carol Dev",    "carol@demo.com",  "USER",  "+91-9000000003"));
            }
            if (productRepo.count() == 0) {
                productRepo.save(new Product("Laptop Pro",    "High-performance laptop",   new BigDecimal("79999"), 15,  "Electronics"));
                productRepo.save(new Product("Wireless Mouse","Ergonomic wireless mouse",  new BigDecimal("1499"),  50,  "Electronics"));
                productRepo.save(new Product("Java Book",     "Learn Java in 30 days",     new BigDecimal("599"),   100, "Books"));
                productRepo.save(new Product("Coffee Mug",    "Developer's best friend",   new BigDecimal("399"),   200, "Accessories"));
                productRepo.save(new Product("USB-C Hub",     "7-in-1 USB hub",            new BigDecimal("2999"),  30,  "Electronics"));
            }
        };
    }
}
