package com.urkejov.inventoryservice.repository;

import com.urkejov.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    @Query("select i from Inventory i where i.skuCode in ?1")
    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
