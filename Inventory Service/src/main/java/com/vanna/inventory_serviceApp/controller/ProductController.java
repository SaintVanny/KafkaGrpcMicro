package com.vanna.inventory_serviceApp.controller;

import com.vanna.inventory_serviceApp.dto.ProductRequest;
import com.vanna.inventory_serviceApp.dto.ProductResponse;
import com.vanna.inventory_serviceApp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.debug("REST request: GET /api/products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        log.debug("REST request: GET /api/products/{}", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.debug("REST request: POST /api/products, body={}", request);
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable UUID id) {
        log.debug("REST request: DELETE /api/products/{}", id);
        productService.deleteProduct(id);
        log.info("Product with ID {} successfully deleted", id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Product with ID " + id + " successfully deleted");
        return ResponseEntity.ok(response);
    }
}