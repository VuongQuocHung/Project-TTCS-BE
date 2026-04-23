package com.laptopshop.service;

import com.laptopshop.entity.*;
import com.laptopshop.repository.BranchRepository;
import com.laptopshop.repository.InventoryLogRepository;
import com.laptopshop.repository.InventoryRepository;
import com.laptopshop.repository.ProductVariantRepository;
import com.laptopshop.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final BranchRepository branchRepository;
    private final ProductVariantRepository variantRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #branchId == principal.branchId)")
    public void updateStock(Long branchId, Long variantId, Integer quantity) {
        InventoryId id = new InventoryId(branchId, variantId);
        Inventory inventory = inventoryRepository.findById(id)
                .orElse(Inventory.builder()
                        .id(id)
                        .quantity(0)
                        .build());

        Integer oldQuantity = inventory.getQuantity();
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);

        // Log the change
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        inventoryLogRepository.save(InventoryLog.builder()
                .branch(branch)
                .variant(variant)
                .oldQuantity(oldQuantity)
                .newQuantity(quantity)
                .action("MANUAL_UPDATE")
                .build());
    }
}
