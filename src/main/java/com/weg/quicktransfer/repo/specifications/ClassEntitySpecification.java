package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.classEntity.ClassEntityFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Course;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ClassEntitySpecification {

    public static Specification<ClassEntity> getFilteredClassEntities(ClassEntityFilter filter) {
        if (filter == null) {
            throw new NullFilterException("Received null filter");
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.course())) {
                Join<ClassEntity, Course> courseJoin = root.join("course", JoinType.INNER);

                Predicate courseNameLike = criteriaBuilder.like(
                        criteriaBuilder.lower(courseJoin.get("name")),
                        "%" + filter.course().toLowerCase() + "%"
                );

                predicates.add(courseNameLike);
            }

            if (filter != null && filter.startDate().)

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
