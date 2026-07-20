package com.weg.quicktransfer.dto.classEntity;

import com.weg.quicktransfer.model.Student;

import java.time.LocalDate;
import java.util.List;

public record ClassEntityResponseDTO(
        Long id,
        String courseName,
        LocalDate finishDate,
        String acronym,
        List<Student> studentNames
) {
}
