package com.ttcs.backend.controller;

import com.ttcs.backend.entity.Brand;
import com.ttcs.backend.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brand API", description = "Quản lý thương hiệu (Brand) sản phẩm")
public class BrandController {
    private final BrandService brandService;

    // 1. GET ALL
    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả thương hiệu", description = "Trả về danh sách toàn bộ thương hiệu hiện có trong hệ thống")
    @ApiResponse(responseCode = "200", description = "Thành công")
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    // 2. POST CREATE
    @PostMapping
    @Operation(summary = "Tạo mới thương hiệu", description = "Thêm một thương hiệu mới vào hệ thống")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
        return ResponseEntity.ok(brandService.createBrand(brand));
    }

    // 3. GET BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin thương hiệu theo ID", description = "Lấy thông tin chi tiết của một thương hiệu dựa trên ID")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    // 4. PUT/PATCH UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thương hiệu", description = "Cập nhật thông tin của một thương hiệu đã tồn tại dựa trên ID")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu")
    public ResponseEntity<Brand> updateBrand(@PathVariable Long id, @RequestBody Brand brand) {
        return ResponseEntity.ok(brandService.updateBrand(id, brand));
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thương hiệu", description = "Xóa một thương hiệu khỏi hệ thống dựa trên ID")
    @ApiResponse(responseCode = "204", description = "Xóa thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy thương hiệu")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
