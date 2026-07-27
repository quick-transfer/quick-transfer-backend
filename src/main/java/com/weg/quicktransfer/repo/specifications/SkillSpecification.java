package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.skill.SkillFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.Skill;
import com.weg.quicktransfer.model.Student;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SkillSpecification {

    public static Specification<Skill> getFilteredInterviews(SkillFilter filter) {
        if (filter == null) {
            throw new NullFilterException("Filter can not be null");
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.name())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("interviewer_name")),
                        "%" + filter.name().toLowerCase() + "%"
                ));
            }

            if (filter.skillType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("skill_type"),
                        filter.skillType()
                ));
            }

            if (filter.grade() != null) {
                predicates.add(criteriaBuilder.equal(
                        (root.get("grade")),
                        filter.grade()
                ));
            }

            if (StringUtils.hasText(filter.studentName())) {
                Join<Skill, Student> studentJoin = root.join("student_id", JoinType.INNER);

                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(studentJoin.get("name")),
                        "%" + filter.studentName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
