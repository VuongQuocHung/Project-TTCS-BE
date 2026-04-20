package com.laptopshop.service;

import com.laptopshop.dto.PageResponseDTO;
import com.laptopshop.dto.ProductDTO;
import com.laptopshop.dto.ProductFilterRequest;
import com.laptopshop.entity.Product;
import com.laptopshop.mapper.ProductMapper;
import com.laptopshop.repository.BrandRepository;
import com.laptopshop.repository.CategoryRepository;
import com.laptopshop.repository.ProductRepository;
import com.laptopshop.repository.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public List<String> getSuggestions(String query) {
        String trimmedQuery = query.trim();
        List<String> suggestions = new java.util.ArrayList<>();
        
        // Add matching product names
        productRepository.findTop10ByNameContainingIgnoreCase(trimmedQuery)
                .forEach(p -> suggestions.add(p.getName()));
        
        // Add matching brand names
        brandRepository.findTop10ByNameContainingIgnoreCase(trimmedQuery)
                .forEach(b -> suggestions.add(b.getName()));
        
        // Add matching category names
        categoryRepository.findTop10ByNameContainingIgnoreCase(trimmedQuery)
                .forEach(c -> suggestions.add(c.getName()));
        
        return suggestions.stream().distinct().limit(10).toList();
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ProductDTO> getProducts(ProductFilterRequest request, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDir() != null ? request.getSortDir() : "DESC"), 
                request.getSortBy() != null ? request.getSortBy() : "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Specification<Product> spec = ProductSpecification.filter(request);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        
        return PageResponseDTO.of(productPage.map(productMapper::toDto));
    }

    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        // Implementation for creation (requires more logic for variants)
        // For now, minimal implementation
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
