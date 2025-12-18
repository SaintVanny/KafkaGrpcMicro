package com.vanna.inventory_serviceApp.service.impl;

import com.vanna.inventory_serviceApp.dto.ProductRequest;
import com.vanna.inventory_serviceApp.dto.ProductResponse;
import com.vanna.inventory_serviceApp.entity.Product;
import com.vanna.inventory_serviceApp.exception.ProductNotFoundException;
import com.vanna.inventory_serviceApp.grpc.inventory.CheckProductAvailabilityResponse;
import com.vanna.inventory_serviceApp.mapper.ProductMapper;
import com.vanna.inventory_serviceApp.repository.ProductRepository;
import com.vanna.inventory_serviceApp.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public CheckProductAvailabilityResponse checkAvailability(String productId, Integer quantity) {
        log.debug("Checking product availability: productId={}, requestedQuantity={}", productId, quantity);

        UUID uuid;
        try {
            uuid = UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", productId);
            return productMapper.toGrpcResponse(null, false, "Invalid product ID format");
        }

        Product product = productRepository.findById(uuid).orElse(null);
        if (product == null) {
            log.warn("Product not found: id={}", productId);
            return productMapper.toGrpcResponse(null, false, "Product not found");
        }

        boolean available = product.getQuantity() >= quantity;
        String message;
        if (available) {
            message = "Product is available";
        } else {
            message = String.format("Insufficient stock. Available: %d, requested: %d", product.getQuantity(), quantity);
        }

        log.info("Availability check result: productId={}, available={}, actualStock={}, price={}, sale={}",
                productId, available, product.getQuantity(), product.getPrice(), product.getSale());

        return productMapper.toGrpcResponse(product, available, message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Getting all products");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products", products.size());
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        log.debug("Getting product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found: id={}", id);
                    return new ProductNotFoundException("Product not found with id: " + id);
                });
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.debug("Creating product: {}", request);

        Product product = productMapper.toEntity(request);
        product.setId(UUID.randomUUID());
        Product savedProduct = productRepository.save(product);

        log.info("Product created: id={}, name={}", savedProduct.getId(), savedProduct.getName());
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        log.debug("Deleting product: id={}", id);

        if (!productRepository.existsById(id)) {
            log.warn("Cannot delete - product not found: id={}", id);
            throw new ProductNotFoundException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted: id={}", id);
    }
}
