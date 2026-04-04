package com.ttcs.backend.service;

import com.ttcs.backend.entity.Product;
import com.ttcs.backend.repository.ProductRepository;
import com.ttcs.backend.specification.ProductSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getFilteredProducts(
            String name, Long brandId, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
            List<String> cpu, List<String> ram, List<String> storage, List<String> vga, List<String> screen,
            Pageable pageable) {

        Specification<Product> spec = Specification.where(ProductSpecs.withFetchSpecs())
                .and(ProductSpecs.hasName(name))
                .and(ProductSpecs.hasBrand(brandId))
                .and(ProductSpecs.hasCategory(categoryId))
                .and(ProductSpecs.priceBetween(minPrice, maxPrice))
                .and(ProductSpecs.hasCpu(cpu))
                .and(ProductSpecs.hasRam(ram))
                .and(ProductSpecs.hasStorage(storage))
                .and(ProductSpecs.hasVga(vga))
                .and(ProductSpecs.hasScreen(screen));

        return productRepository.findAll(spec, pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setImportPrice(productDetails.getImportPrice());
        product.setStock(productDetails.getStock());
        product.setDescription(productDetails.getDescription());
        product.setBrand(productDetails.getBrand());
        product.setCategory(productDetails.getCategory());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
