package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;
import java.util.Locale;

import com.weg.quicktransfer.dto.student.StudentRequestDTO;
import com.weg.quicktransfer.dto.student.StudentResponseDTO;
import com.weg.quicktransfer.enums.StudentInterviewStatus;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Student;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class StudentMapper {
    public Student toEntity(StudentRequestDTO studentRequestDTO, ClassEntity classEntity) {
        return new Student(
            studentRequestDTO.name(),
            studentRequestDTO.email(),
            normalizeRegistration(studentRequestDTO.registration(), studentRequestDTO.email()),
            studentRequestDTO.attendanceRate() == null ? 0.0 : studentRequestDTO.attendanceRate(),
            studentRequestDTO.age(),
            studentRequestDTO.averageGrade(),
            classEntity,
            StudentInterviewStatus.valueOf(
                    studentRequestDTO.statusStudentInterview().trim().toUpperCase(Locale.ROOT)),
            studentRequestDTO.hasSeenEmail(),
            null
        );
    }

    public StudentResponseDTO toResponse(Student student) {
        return new StudentResponseDTO(
            student.getId(),
            student.getName(),
            student.getEmail(),
            student.getAge(),
            student.getAverageGrade(),
            student.getClassEntity().getAcronym(),
            student.getClassEntity().getCourse().getName(),
            student.getStatus().name(),
            student.getHasSeenEmail(),
            student.getStatusStudent().name(),
            student.getRegistration(),
            student.getAttendanceRate(),
            student.getClassEntity().getName(),
            student.getClassEntity().getShiftClass().name(),
            student.getAverageGrade()
        );
    }

    public String normalizeRegistration(String registration, String email) {
        if (registration != null && !registration.isBlank()) {
            return registration.trim().toUpperCase(Locale.ROOT);
        }
        UUID stableId = UUID.nameUUIDFromBytes(email.trim().toLowerCase(Locale.ROOT)
                .getBytes(StandardCharsets.UTF_8));
        return "STU-" + stableId.toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
