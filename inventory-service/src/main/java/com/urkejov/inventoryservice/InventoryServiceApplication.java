package com.urkejov.inventoryservice;

import com.urkejov.inventoryservice.model.Inventory;
import com.urkejov.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }


    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {

            inventoryRepository.save(Inventory.builder()
                    .skuCode("ThinkPad 15")
                    .quantity(100)
                    .build());

            inventoryRepository.save(Inventory.builder()
                    .skuCode("Lenovo Legion")
                    .quantity(20)
                    .build());
        };
    }
}
