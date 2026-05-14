package com.laptopshop.repository;
import com.laptopshop.entity.Order;
import com.laptopshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserId(Long userId);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    List<Order> findByBranchId(Long branchId);
    boolean existsByUserIdAndItems_Variant_ProductIdAndStatus(Long userId, Long productId, OrderStatus status);
}
