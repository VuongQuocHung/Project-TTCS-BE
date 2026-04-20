package com.laptopshop.service;

import com.laptopshop.dto.*;
import com.laptopshop.entity.Branch;
import com.laptopshop.entity.Inventory;
import com.laptopshop.entity.InventoryId;
import com.laptopshop.mapper.BranchMapper;
import com.laptopshop.repository.BranchRepository;
import com.laptopshop.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public List<BranchFulfillmentDTO> checkCartFulfillment(CartCheckRequest request) {
        List<Branch> allBranches = branchRepository.findAll();
        List<BranchFulfillmentDTO> results = new ArrayList<>();

        for (Branch branch : allBranches) {
            List<ItemFulfillmentDTO> itemStatuses = new ArrayList<>();
            int availableCount = 0;

            for (CartCheckRequest.CartItemRequest itemReq : request.getItems()) {
                InventoryId id = new InventoryId(branch.getId(), itemReq.getVariantId());
                Integer available = inventoryRepository.findById(id)
                        .map(Inventory::getQuantity)
                        .orElse(0);

                boolean isAvailable = available >= itemReq.getQuantity();
                if (isAvailable) {
                    availableCount++;
                }

                itemStatuses.add(ItemFulfillmentDTO.builder()
                        .variantId(itemReq.getVariantId())
                        .requestedQuantity(itemReq.getQuantity())
                        .availableQuantity(available)
                        .isAvailable(isAvailable)
                        .build());
            }

            FulfillmentStatus status;
            if (availableCount == request.getItems().size()) {
                status = FulfillmentStatus.FULLY_AVAILABLE;
            } else if (availableCount > 0) {
                status = FulfillmentStatus.PARTIALLY_AVAILABLE;
            } else {
                status = FulfillmentStatus.UNAVAILABLE;
            }

            results.add(BranchFulfillmentDTO.builder()
                    .branchId(branch.getId())
                    .branchName(branch.getName())
                    .address(branch.getAddress())
                    .phone(branch.getPhone())
                    .status(status)
                    .items(itemStatuses)
                    .build());
        }

        // Sort: Full fulfillment first
        return results.stream()
                .sorted((a, b) -> a.getStatus().compareTo(b.getStatus()))
                .collect(Collectors.toList());
    }

    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(branchMapper::toDto)
                .collect(Collectors.toList());
    }

    public BranchDTO getBranchById(Long id) {
        return branchRepository.findById(id)
                .map(branchMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
    }

    @Transactional
    public BranchDTO createBranch(BranchDTO dto) {
        Branch branch = Branch.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .build();
        return branchMapper.toDto(branchRepository.save(branch));
    }

    @Transactional
    public BranchDTO updateBranch(Long id, BranchDTO dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        branch.setName(dto.getName());
        branch.setAddress(dto.getAddress());
        branch.setPhone(dto.getPhone());
        return branchMapper.toDto(branchRepository.save(branch));
    }

    @Transactional
    public void deleteBranch(Long id) {
        branchRepository.deleteById(id);
    }
}
