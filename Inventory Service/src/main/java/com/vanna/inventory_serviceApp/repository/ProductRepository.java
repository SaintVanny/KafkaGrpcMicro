package com.vanna.inventory_serviceApp.repository;

import com.vanna.inventory_serviceApp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.quantity >= :requiredQuantity")
    Optional<Product> findByIdAndCheckQuantity(
            @Param("id") UUID id,
            @Param("requiredQuantity") Integer requiredQuantity
    );

    boolean existsById(UUID id);
}