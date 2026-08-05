package com.weg.quicktransfer.model;

import com.weg.quicktransfer.enums.SkillType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vacancy_skills")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VacancySkill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false)
    private SkillType skillType;

    @Column(name = "minimum_grade", nullable = false)
    private Double minimumGrade;

    @ManyToMany(mappedBy = "skills")
    private List<Vacancy> vacancies = new ArrayList<>();

    public VacancySkill(String name, SkillType skillType, Double minimumGrade) {
        this.name = name;
        this.skillType = skillType;
        this.minimumGrade = minimumGrade;
    }
}
