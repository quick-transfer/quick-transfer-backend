package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.place.PlaceFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.Place;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PlaceSpecification {

    public static Specification<Place> getFilteredPlaces(PlaceFilter filter) {
        if (filter == null) {
            throw new NullFilterException("Filter can not be null");
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.placeName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("placeName")),
                        "%" + filter.placeName().toLowerCase() + "%"
                ));
            }

            if (filter.park() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("park"),
                        filter.park()
                ));
            }

            if (filter.section() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("section"),
                        filter.section()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
