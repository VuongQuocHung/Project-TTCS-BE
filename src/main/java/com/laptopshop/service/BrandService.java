package com.laptopshop.service;

import com.laptopshop.dto.PageResponseDTO;
import com.laptopshop.dto.BrandDTO;
import com.laptopshop.entity.Brand;
import com.laptopshop.mapper.BrandMapper;
import com.laptopshop.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public PageResponseDTO<BrandDTO> getBrands(int page, int size, boolean isAdmin) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Brand> brandPage = brandRepository.findAll(com.laptopshop.repository.specification.BrandSpecification.filter(isAdmin), pageable);
        return PageResponseDTO.of(brandPage.map(brandMapper::toDto));
    }

    public List<BrandDTO> getAllBrands(boolean isAdmin) {
        return brandRepository.findAll(com.laptopshop.repository.specification.BrandSpecification.filter(isAdmin)).stream()
                .map(brandMapper::toDto)
                .collect(Collectors.toList());
    }

    public BrandDTO getBrandById(Long id) {
        return brandRepository.findById(id)
                .map(brandMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    @Transactional
    public BrandDTO createBrand(BrandDTO dto) {
        Brand brand = Brand.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        return brandMapper.toDto(brandRepository.save(brand));
    }

    @Transactional
    public BrandDTO updateBrand(Long id, BrandDTO dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        brand.setName(dto.getName());
        brand.setLogo(dto.getLogo());
        brand.setDescription(dto.getDescription());
        return brandMapper.toDto(brandRepository.save(brand));
    }

    @Transactional
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }
}
