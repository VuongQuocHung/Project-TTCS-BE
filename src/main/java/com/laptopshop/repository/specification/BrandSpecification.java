package com.laptopshop.repository.specification;

import com.laptopshop.entity.Brand;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BrandSpecification {
    public static Specification<Brand> filter(boolean isAdmin) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!isAdmin) {
                predicates.add(cb.equal(root.get("enabled"), true));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
