package com.laptopshop.service;

import com.laptopshop.dto.PageResponseDTO;
import com.laptopshop.dto.CategoryDTO;
import com.laptopshop.entity.Category;
import com.laptopshop.mapper.CategoryMapper;
import com.laptopshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public PageResponseDTO<CategoryDTO> getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return PageResponseDTO.of(categoryPage.map(categoryMapper::toDto));
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .slug(dto.getName().toLowerCase().replace(" ", "-"))
                .description(dto.getDescription())
                .build();
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.getName());
        category.setSlug(dto.getName().toLowerCase().replace(" ", "-"));
        category.setDescription(dto.getDescription());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
