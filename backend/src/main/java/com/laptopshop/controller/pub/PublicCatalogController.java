package com.laptopshop.controller.pub;

import com.laptopshop.dto.BranchDTO;
import com.laptopshop.dto.BranchFulfillmentDTO;
import com.laptopshop.dto.BrandDTO;
import com.laptopshop.dto.CartCheckRequest;
import com.laptopshop.dto.CategoryDTO;
import com.laptopshop.service.BranchService;
import com.laptopshop.service.BrandService;
import com.laptopshop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Quản lý Danh mục & Thương hiệu (Catalog)")
public class PublicCatalogController {

    private final CategoryService categoryService;
    private final BrandService brandService;
    private final BranchService branchService;

    @GetMapping("/categories")
    public List<CategoryDTO> getCategories() {
        return categoryService.getAllCategories(false);
    }

    @GetMapping("/brands")
    public List<BrandDTO> getBrands() {
        return brandService.getAllBrands();
    }

    @GetMapping("/branches")
    public List<BranchDTO> getBranches() {
        return branchService.getAllBranches();
    }

    @PostMapping("/branches/check-availability")
    @Operation(summary = "Kiểm tra khả năng đáp ứng giỏ hàng của các chi nhánh")
    public List<BranchFulfillmentDTO> checkAvailability(@RequestBody CartCheckRequest request) {
        return branchService.checkCartFulfillment(request);
    }
}
