package com.laptopshop.repository;
import com.laptopshop.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
    List<Brand> findTop10ByNameContainingIgnoreCase(String name);
}
