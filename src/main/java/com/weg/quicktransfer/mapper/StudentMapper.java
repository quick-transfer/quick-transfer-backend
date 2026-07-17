package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.student.StudentRequestDTO;
import com.weg.quicktransfer.dto.student.StudentResponseDTO;
import com.weg.quicktransfer.enums.StudentInterviewStatus;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Student;

@Component
public class StudentMapper {
    public Student toEntity(StudentRequestDTO studentRequestDTO, ClassEntity classEntity) {
        return new Student(
            studentRequestDTO.name(),
            studentRequestDTO.email(),
            studentRequestDTO.averageGrade(),
            classEntity,
            StudentInterviewStatus.valueOf(studentRequestDTO.statusStudent()),
            studentRequestDTO.hasSeenEmail(),
            null
        );
    }

    public StudentResponseDTO toResponse(Student student) {
        return new StudentResponseDTO(
            student.getId(),
            student.getName(),
            student.getEmail(),
            student.getAverageGrade(),
            student.getClassEntity().getAcronym(),
            student.getClassEntity().getCourse().getName(),
            student.getStatus().name(),
            student.getHasSeenEmail()
        );
    }
}
