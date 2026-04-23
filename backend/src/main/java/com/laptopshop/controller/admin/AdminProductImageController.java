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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products/upload-image")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Product", description = "Quản lý sản phẩm (Products)")
public class AdminProductImageController {

    private final FileService fileService;

    @Operation(summary = "Upload image file (multipart/form-data)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileService.saveFile(file);
        String fileUrl = "/uploads/" + fileName;
        
        return ResponseEntity.ok(Map.of(
            "url", fileUrl,
            "fileName", fileName
        ));
    }
}
