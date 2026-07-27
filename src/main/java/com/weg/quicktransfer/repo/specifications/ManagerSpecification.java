package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.manager.ManagerFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.Manager;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ManagerSpecification {
    public static Specification<Manager> getFilteredManagers(ManagerFilter filter) {
        if (filter == null) {
            throw new NullFilterException("Filter can not be null");
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.section() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("section"),
                        filter.section().toString()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
