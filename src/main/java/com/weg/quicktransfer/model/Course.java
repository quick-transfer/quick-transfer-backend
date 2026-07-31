package com.weg.quicktransfer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinator_id", nullable = false)
    private Coordinator coordinator;

    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ClassEntity> classes = new ArrayList<>();

    public Course(String name, Coordinator coordinator, List<ClassEntity> classes) {
        this.name = name;
        this.coordinator = coordinator;
        this.classes = classes;
    }

    public Course(String name, Coordinator coordinator) {
        this.name = name;
        this.coordinator = coordinator;
    }
}
