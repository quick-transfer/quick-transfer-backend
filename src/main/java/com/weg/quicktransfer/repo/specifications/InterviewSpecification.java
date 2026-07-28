package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class InterviewSpecification {

    public static Specification<Interview> getFilteredInterviews(InterviewFilter filter) {
        if (filter == null) {
            throw new NullFilterException("Filter can not be null");
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.interviewerName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("interviewerName")),
                        "%" + filter.interviewerName().toLowerCase() + "%"
                ));
            }

            if (filter.dateTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("dateTime"),
                        filter.dateTime()
                ));
            }

            if (StringUtils.hasText(filter.vacancyName())) {
                Join<Interview, Vacancy> vacancyJoin = root.join("vacancy", JoinType.INNER);

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(vacancyJoin.get("name")),
                        "%" + filter.vacancyName().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.placeName())) {
                Join<Interview, Place> vacancyJoin = root.join("place", JoinType.INNER);

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(vacancyJoin.get("name")),
                        "%" + filter.placeName().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.managerName())) {
                Join<Interview, Manager> managerJoin = root.join("vacancy_id", JoinType.INNER);

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(managerJoin.get("name")),
                        "%" + filter.interviewerName().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.studentName())) {
                Join<Interview, Student> placeJoin = root.join("student", JoinType.INNER);

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(placeJoin.get("placeName")),
                        "%" + filter.studentName().toLowerCase() + "%"
                ));
            }

            if (filter.reminderSent() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("reminderSent")),
                        filter.reminderSent().toString().toLowerCase()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
