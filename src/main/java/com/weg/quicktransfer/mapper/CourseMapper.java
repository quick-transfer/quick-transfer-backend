package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.enums.EntityStatus;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CourseMapper {

    public Course toEntity(CourseRequestDTO courseRequestDTO, Coordinator coordinator){
        return new Course(
                courseRequestDTO.name(),
                normalizeCode(courseRequestDTO.code(), courseRequestDTO.name()),
                parseStatus(courseRequestDTO.status()),
                coordinator
        );
    }
    //
    public CourseResponseDTO toResponse(Course course){
        return new CourseResponseDTO(
                course.getId(),
                course.getName(),
                course.getCoordinator().getName(),
                course.getCoordinator().getEmail(),
                course.getCode(),
                course.getStatus().name(),
                course.getClasses().stream()
                        .mapToLong(classEntity -> classEntity.getStudents().size())
                        .sum()
        );
    }

    public String normalizeCode(String code, String fallback) {
        String source = code == null || code.isBlank() ? fallback : code;
        return source.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public EntityStatus parseStatus(String status) {
        return status == null || status.isBlank()
                ? EntityStatus.ACTIVE
                : EntityStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
    }
}
