package com.laptopshop.repository.specification;

import com.laptopshop.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> filter(com.laptopshop.dto.UserFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), filter.getRole()));
            }

            if (filter.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), filter.getEnabled()));
            }

            if (filter.getBranchId() != null) {
                predicates.add(cb.equal(root.get("branch").get("id"), filter.getBranchId()));
            }

            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String keyword = "%" + filter.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), keyword),
                        cb.like(cb.lower(root.get("email")), keyword),
                        cb.like(cb.lower(root.get("fullName")), keyword)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
