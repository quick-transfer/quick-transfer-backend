package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public Course toEntity(CourseRequestDTO courseRequestDTO, Coordinator coordinator){
        return new Course(
                courseRequestDTO.name(),
                coordinator
        );
    }
    //
    public CourseResponseDTO toResponse(Course course){
        return new CourseResponseDTO(
                course.getId(),
                course.getName(),
                course.getCoordinator().getName(),
                course.getCoordinator().getEmail()
        );
    }
}
