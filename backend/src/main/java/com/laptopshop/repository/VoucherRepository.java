package com.laptopshop.repository;

import com.laptopshop.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    java.util.List<Voucher> findByTargetUserIdOrTargetUserIsNull(Long targetUserId);
}
