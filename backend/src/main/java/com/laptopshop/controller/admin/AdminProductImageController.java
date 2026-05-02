package com.laptopshop.controller.admin;

import com.laptopshop.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/products/upload-image")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Product", description = "Quản lý sản phẩm (Products)")
public class AdminProductImageController {

    private final FileService fileService;

    @Operation(summary = "Upload multiple image files (multipart/form-data)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<java.util.List<java.util.Map<String, String>>> uploadFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        java.util.List<java.util.Map<String, String>> results = new java.util.ArrayList<>();
        
        for (MultipartFile file : files) {
            String fileName = fileService.saveFile(file);
            String fileUrl = "/uploads/" + fileName;
            results.add(java.util.Map.of(
                "url", fileUrl,
                "fileName", fileName
            ));
        }
        
        return ResponseEntity.ok(results);
    }
}
