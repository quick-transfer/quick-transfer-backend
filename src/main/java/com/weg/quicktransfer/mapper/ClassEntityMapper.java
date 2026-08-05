package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.enums.ShiftClass;
import com.weg.quicktransfer.enums.StatusClass;
import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Course;

import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class ClassEntityMapper {

    public ClassEntity toEntity(ClassEntityRequestDTO classEntityResquestDTO, Course course){
        return new ClassEntity(
                course,
                classEntityResquestDTO.startDate(),
                classEntityResquestDTO.finishDate(),
                StatusClass.valueOf(classEntityResquestDTO.status().trim().toUpperCase(Locale.ROOT)),
                ShiftClass.valueOf(classEntityResquestDTO.shiftClass().trim().toUpperCase(Locale.ROOT)),
                classEntityResquestDTO.acronym()
        );
    }

    public ClassEntityResponseDTO toResponse(ClassEntity classEntity){
        return new ClassEntityResponseDTO(
                classEntity.getId(),
                classEntity.getCourse().getName(),
                classEntity.getStartDate(),
                classEntity.getFinishDate(),
                classEntity.getStatus().name(),
                classEntity.getShiftClass().name(),
                classEntity.getAcronym()
        );
    }
}
