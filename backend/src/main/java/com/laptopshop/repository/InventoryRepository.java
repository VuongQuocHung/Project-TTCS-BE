package com.laptopshop.repository;

import com.laptopshop.entity.Inventory;
import com.laptopshop.entity.InventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {
    List<Inventory> findByBranchIdAndQuantityLessThan(Long branchId, Integer quantity);
    List<Inventory> findByQuantityLessThan(Integer quantity);
}
