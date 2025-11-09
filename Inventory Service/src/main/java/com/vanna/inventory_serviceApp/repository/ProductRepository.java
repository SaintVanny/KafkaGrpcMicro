package com.vanna.inventory_serviceApp.repository;

import com.vanna.inventory_serviceApp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByName(String name);
    
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.quantity >= :requiredQuantity")
    Optional<Product> findByIdAndCheckQuantity(
            @Param("id") Long id,
            @Param("requiredQuantity") Integer requiredQuantity
    );
    
    boolean existsById(Long id);
}