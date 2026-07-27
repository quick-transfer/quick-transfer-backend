package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.classEntity.ClassEntityFilter;
import com.weg.quicktransfer.exception.DateOutOfRangeException;
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

                Predicate classEntityNameLike = criteriaBuilder.like(
                        criteriaBuilder.lower(courseJoin.get("name")),
                        "%" + filter.course().toLowerCase() + "%"
                );

                predicates.add(classEntityNameLike);
            }

            if (filter.startDate() != null && filter.finishDate() != null && filter.startDate().isAfter(filter.finishDate())) {
                throw new DateOutOfRangeException("Finish date can not be before start date");
            }

            if (filter.startDate() != null) {
                Predicate startDateAfter = criteriaBuilder.greaterThan(
                        root.get("start_date"),
                        filter.startDate()
                );
            }

            if (filter.startDate() != null) {
                Predicate finishDateBefore = criteriaBuilder.lessThan(
                        root.get("finish_date"),
                        filter.finishDate()
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
