package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.mapper.CourseMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.repo.CourseRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CoordinatorService coordinatorService;

    //returns a new Course
    public CourseResponseDTO create(CourseRequestDTO courseRequestDTO){
        //throws exception if a Course exists with that name
        if(courseRepository.existsByName(courseRequestDTO.name())){
            throw new CourseNotFoundException("Course already exists with this name");
        }
        //finds coordinator by id
        Coordinator coordinator = coordinatorService.findById(courseRequestDTO.coordinatorId());
        //transform CourseRequestDTO to entity
        Course course = courseMapper.toEntity(courseRequestDTO, coordinator);
        //saves course
        courseRepository.save(course);
        List<String> classesAcronym = new ArrayList<>();
        //returns CourseResponseDTO with empty list of class acronyms
        return courseMapper.toResponse(course, classesAcronym);
    }

    public List<CourseResponseDTO> findAll(){
        //search all courses
        List<Course> courses = courseRepository.findAll();
        //initializes list
        List<CourseResponseDTO> courseResponseDTOS = new ArrayList<>();
        //goes through courses transforming each one to CourseResponseDTO with the list of class acronyms
        for(Course course : courses){
            List<String> classesAcronym = new ArrayList<>();
            //Creates a list of the class acronyms
            for (ClassEntity classEntity : course.getClasses()){
                classesAcronym.add(classEntity.getAcronym());
            }
            courseResponseDTOS.add(courseMapper.toResponse(course, classesAcronym));
        }
        return courseResponseDTOS;
    }
}
