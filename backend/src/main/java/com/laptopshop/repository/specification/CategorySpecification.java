package com.laptopshop.repository.specification;

import com.laptopshop.entity.Category;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {
    public static Specification<Category> filter(boolean isAdmin) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!isAdmin) {
                predicates.add(cb.equal(root.get("enabled"), true));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
