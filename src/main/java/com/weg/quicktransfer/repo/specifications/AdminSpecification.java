package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.model.Admin;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminSpecification {

    public static Specification<Admin> getFilteredAdmins(AdminFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.name())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.name().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.username())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        "%" + filter.username().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
