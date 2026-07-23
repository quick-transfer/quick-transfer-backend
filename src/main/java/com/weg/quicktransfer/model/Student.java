package com.weg.quicktransfer.model;

import com.weg.quicktransfer.enums.StudentInterviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Student{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Long age;

    @Column(name = "average_grade")
    private Double averageGrade;

    @ManyToOne
    @JoinColumn(name = "classentity_id", nullable = false)
    private ClassEntity classEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentInterviewStatus status;

    @Column(nullable = false, name = "has_seen_email")
    private Boolean hasSeenEmail;

    @OneToOne(mappedBy = "student")
    @JoinColumn(name = "interview_id")
    private Interview interview;

    public Student(String name, String email, Long age, Double averageGrade, ClassEntity classEntity,
            StudentInterviewStatus status, Boolean hasSeenEmail, Interview interview) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.averageGrade = averageGrade;
        this.classEntity = classEntity;
        this.status = status;
        this.hasSeenEmail = hasSeenEmail;
        this.interview = interview;
    }
}
