package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.vacancy.VacancySkillFilter;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.VacancySkill;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VacancySkillSpecification {

    private VacancySkillSpecification() {
    }

    public static Specification<VacancySkill> getFilteredSkills(VacancySkillFilter filter) {
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

            if (filter.skillType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("skillType"), filter.skillType()));
            }

            if (filter.minimumGrade() != null) {
                predicates.add(criteriaBuilder.equal(root.get("minimumGrade"), filter.minimumGrade()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
