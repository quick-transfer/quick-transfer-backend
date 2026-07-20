package com.weg.quicktransfer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class_entities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "finish_date", nullable = false)
    private LocalDate finishDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    List<Student> students = new ArrayList<>();

    @Column(nullable = false)
    private String acronym;

    public ClassEntity(Course course, LocalDate startDate, LocalDate finishDate, List<Student> students, String acronym) {
        this.course = course;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.students = students;
        this.acronym = acronym;
    }

    public ClassEntity(Course course, LocalDate startDate, LocalDate finishDate, String acronym) {
        this.course = course;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.acronym = acronym;
    }
}
