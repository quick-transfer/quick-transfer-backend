package com.weg.quicktransfer.model;

import java.util.ArrayList;
import java.util.List;

import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

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

    public Vacancy(String name, String description, Area area, Shift shift, Place place) {
        this.name = name;
        this.description = description;
        this.area = area;
        this.shift = shift;
        this.place = place;
    }
}