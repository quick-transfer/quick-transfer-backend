package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.vacancy.VacancyFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VacancySpecification {

    public static Specification<Vacancy> getFilteredVacancies(VacancyFilter filter) {
        if (filter == null) {
            throw new NullFilterException("Filter can not be null");
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.name())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.name().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.description())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + filter.description().toLowerCase() + "%"
                ));
            }

            if (filter.numberVacancies() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("numberVacancies"),
                        filter.numberVacancies()
                ));
            }

            if (filter.area() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("area"),
                        filter.area()
                ));
            }

            if (filter.shift() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("shift"),
                        filter.shift()
                ));
            }

            if (StringUtils.hasText(filter.placeName())) {
                Join<Vacancy, Place> placeJoin = root.join("place", JoinType.INNER);

                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(placeJoin.get("name")),
                        filter.placeName().toLowerCase()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
