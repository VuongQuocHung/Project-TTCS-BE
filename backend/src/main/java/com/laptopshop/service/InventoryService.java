package com.laptopshop.service;

import com.laptopshop.entity.Inventory;
import com.laptopshop.entity.InventoryId;
import com.laptopshop.repository.InventoryRepository;
import com.laptopshop.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #branchId == principal.branchId)")
    public void updateStock(Long branchId, Long variantId, Integer quantity) {
        InventoryId id = new InventoryId(branchId, variantId);
        Inventory inventory = inventoryRepository.findById(id)
                .orElse(Inventory.builder()
                        .id(id)
                        .quantity(0)
                        .build());

        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
    }
}
