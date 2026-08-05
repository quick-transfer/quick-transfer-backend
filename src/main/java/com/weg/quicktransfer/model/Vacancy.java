package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vacancies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, name = "numbers_vacancies")
    private Long numbersVacancies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Area area;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "vacancy")
    private List<Interview> interviews = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "vacancy_skill_assignments",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "vacancy_skill_id")
    )
    @OrderBy("name ASC")
    private List<VacancySkill> skills = new ArrayList<>();

    public Vacancy(String name, String description, Long numbersVacancies, Area area, Shift shift, Place place) {
        this.name = name;
        this.description = description;
        this.numbersVacancies = numbersVacancies;
        this.area = area;
        this.shift = shift;
        this.place = place;
    }

    public void setSkills(List<VacancySkill> skills) {
        new ArrayList<>(this.skills).forEach(this::removeSkill);
        if (skills != null) {
            skills.forEach(this::addSkill);
        }
    }

    public void addSkill(VacancySkill skill) {
        if (skill != null && !this.skills.contains(skill)) {
            this.skills.add(skill);
            if (!skill.getVacancies().contains(this)) {
                skill.getVacancies().add(this);
            }
        }
    }

    public void removeSkill(VacancySkill skill) {
        if (skill != null && this.skills.remove(skill)) {
            skill.getVacancies().remove(this);
        }
    }
}
