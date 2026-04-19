package com.laptopshop.controller.pub;

import com.laptopshop.dto.BrandDTO;
import com.laptopshop.dto.CategoryDTO;
import com.laptopshop.service.BrandService;
import com.laptopshop.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Quản lý Danh mục & Thương hiệu (Catalog)")
public class PublicCatalogController {

    private final CategoryService categoryService;
    private final BrandService brandService;

    @GetMapping("/categories")
    public List<CategoryDTO> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/brands")
    public List<BrandDTO> getBrands() {
        return brandService.getAllBrands();
    }
}
