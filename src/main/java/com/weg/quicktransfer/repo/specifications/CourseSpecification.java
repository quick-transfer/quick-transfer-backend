package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.course.CourseFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {

    public static Specification<Course> getFilteredCourses(CourseFilter filter) {
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

            if (StringUtils.hasText(filter.coordinatorName())) {
                Join<Course, Coordinator> coordinatorJoin = root.join("coordinator", JoinType.INNER);

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(coordinatorJoin.get("name")),
                        "%" + filter.coordinatorName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}