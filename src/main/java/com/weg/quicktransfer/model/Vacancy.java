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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "numbers_vacancies")
    private Long numbersVacancies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Area area;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Shift shift;

    @ManyToOne
    @JoinColumn(nullable = false, name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "vacancy")
    private List<Interview> interviews = new ArrayList<>();

    public Vacancy(String name, String description, Long numbersVacancies, Area area, Shift shift, Place place) {
        this.name = name;
        this.description = description;
        this.numbersVacancies = numbersVacancies;
        this.area = area;
        this.shift = shift;
        this.place = place;
    }
}