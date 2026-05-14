package com.laptopshop.service;

import com.laptopshop.dto.PageResponseDTO;
import com.laptopshop.dto.VoucherDTO;
import com.laptopshop.entity.User;
import com.laptopshop.entity.Voucher;
import com.laptopshop.entity.VoucherStatus;
import com.laptopshop.exception.ResourceNotFoundException;
import com.laptopshop.mapper.VoucherMapper;
import com.laptopshop.repository.UserRepository;
import com.laptopshop.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final UserRepository userRepository;

    public PageResponseDTO<VoucherDTO> getAllVouchers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Voucher> voucherPage = voucherRepository.findAll(pageable);
        return PageResponseDTO.<VoucherDTO>builder()
                .content(voucherPage.getContent().stream().map(voucherMapper::toDto).toList())
                .pageNo(voucherPage.getNumber())
                .pageSize(voucherPage.getSize())
                .totalElements(voucherPage.getTotalElements())
                .totalPages(voucherPage.getTotalPages())
                .last(voucherPage.isLast())
                .build();
    }

    public VoucherDTO getVoucherById(Long id) {
        return voucherRepository.findById(id)
                .map(voucherMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));
    }

    public VoucherDTO getVoucherByCode(String code) {
        return voucherRepository.findByCode(code)
                .map(voucherMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher code not found"));
    }

    @Transactional
    public VoucherDTO createVoucher(VoucherDTO dto) {
        if (voucherRepository.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Voucher code already exists");
        }
        Voucher voucher = voucherMapper.toEntity(dto);
        voucher.setUsedCount(0);
        if (voucher.getStatus() == null) {
            voucher.setStatus(VoucherStatus.ACTIVE);
        }
        
        if (dto.getTargetUserId() != null) {
            User targetUser = userRepository.findById(dto.getTargetUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
            voucher.setTargetUser(targetUser);
        }

        return voucherMapper.toDto(voucherRepository.save(voucher));
    }

    @Transactional
    public VoucherDTO updateVoucher(Long id, VoucherDTO dto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));
        
        voucher.setCode(dto.getCode());
        voucher.setDiscountType(dto.getDiscountType());
        voucher.setDiscountValue(dto.getDiscountValue());
        voucher.setMinOrderValue(dto.getMinOrderValue());
        voucher.setMaxDiscountValue(dto.getMaxDiscountValue());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());
        voucher.setUsageLimit(dto.getUsageLimit());
        voucher.setStatus(dto.getStatus());
        
        if (dto.getTargetUserId() != null) {
            User targetUser = userRepository.findById(dto.getTargetUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
            voucher.setTargetUser(targetUser);
        } else {
            voucher.setTargetUser(null);
        }
        
        return voucherMapper.toDto(voucherRepository.save(voucher));
    }

    @Transactional
    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }

    public java.util.List<VoucherDTO> getMyVouchers(Long userId) {
        return voucherRepository.findByTargetUserIdOrTargetUserIsNull(userId).stream()
                .filter(v -> v.getStatus() == VoucherStatus.ACTIVE)
                .map(voucherMapper::toDto)
                .toList();
    }

    public Voucher validateVoucher(String code, Double orderValue) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher code not found"));

        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new RuntimeException("Voucher is not active");
        }

        // Check targeted user
        Long currentUserId = com.laptopshop.security.SecurityUtils.getCurrentUserId();
        if (voucher.getTargetUser() != null && !voucher.getTargetUser().getId().equals(currentUserId)) {
            throw new RuntimeException("Mã giảm giá này không dành cho bạn!");
        }

        LocalDateTime now = LocalDateTime.now();
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new RuntimeException("Voucher is not yet available");
        }
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            voucher.setStatus(VoucherStatus.EXPIRED);
            voucherRepository.save(voucher);
            throw new RuntimeException("Voucher has expired");
        }

        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            voucher.setStatus(VoucherStatus.EXHAUSTED);
            voucherRepository.save(voucher);
            throw new RuntimeException("Voucher usage limit reached");
        }

        if (voucher.getMinOrderValue() != null && orderValue < voucher.getMinOrderValue()) {
            throw new RuntimeException("Order value is below minimum required for this voucher: " + voucher.getMinOrderValue());
        }

        return voucher;
    }

    @Transactional
    public void useVoucher(Voucher voucher) {
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            voucher.setStatus(VoucherStatus.EXHAUSTED);
        }
        voucherRepository.save(voucher);
    }
}
