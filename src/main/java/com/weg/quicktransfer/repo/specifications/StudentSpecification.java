package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.student.StudentFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StudentSpecification {

    public static Specification<Student> getFilteredStudents(StudentFilter filter) {
        if (filter == null) {
            throw new NullFilterException("Filter can not be null");
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.name())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        filter.name()
                ));
            }

            if (StringUtils.hasText(filter.email())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        filter.email()
                ));
            }

            if (filter.age() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("age"),
                        filter.age()
                ));
            }

            if (filter.averageGrade() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("average_grade"),
                        filter.averageGrade()
                ));
            }

            if (StringUtils.hasText(filter.name())) {
                Join<Student, ClassEntity> classEntityJoin = root.join("classEntity", JoinType.INNER);
                Join<ClassEntity, Course> courseJoin = classEntityJoin.join("course", JoinType.INNER);

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(courseJoin.get("name")),
                        "%" + filter.courseName().toLowerCase() + "%"
                ));
            }

            if (filter.studentInterviewStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        filter.name()
                ));
            }

            if (filter.hasSeenEmail() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("hasSeenEmail")),
                        filter.hasSeenEmail().toString().toLowerCase()
                ));
            }

            if (filter.statusStudent() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("statusStudent"),
                        filter.name()
                ));
            }

            if (StringUtils.hasText(filter.name())) {
                Join<Student, Interview> interviewJoin = root.join("classEntity", JoinType.INNER);

                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        interviewJoin.get("dateTime"),
                        filter.interviewDateTime()
                ));
            }

            if (filter.skillsName() != null && !filter.skillsName().isEmpty()) {
                Join<Student, Skill> skillJoin = root.join("skills", JoinType.INNER);

                List<Predicate> skillPredicates = new ArrayList<>();
                for (String skill : filter.skillsName()) {
                    skillPredicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(skillJoin.get("name")),
                            "%" + skill.toLowerCase() + "%"
                    ));
                }

                predicates.add(criteriaBuilder.or(skillPredicates.toArray(new Predicate[0])));

                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
