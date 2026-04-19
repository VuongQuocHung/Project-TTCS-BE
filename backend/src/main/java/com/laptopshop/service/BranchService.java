package com.laptopshop.service;

import com.laptopshop.dto.BranchDTO;
import com.laptopshop.entity.Branch;
import com.laptopshop.mapper.BranchMapper;
import com.laptopshop.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

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
