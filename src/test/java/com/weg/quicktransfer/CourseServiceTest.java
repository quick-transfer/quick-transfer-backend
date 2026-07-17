package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.CourseRequestDTO;
import com.weg.quicktransfer.dto.CourseResponseDTO;
import com.weg.quicktransfer.mapper.CourseMapper;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.repo.CourseRepo;
import com.weg.quicktransfer.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepo courseRepo;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseRequestDTO requestDTO;
    private CourseResponseDTO responseDTO;
    private Coordinator coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new Coordinator();
        coordinator.setId(1L);

        course = new Course();
        course.setId(1L);
        course.setName("Java");
        course.setCoordinator(coordinator);
        course.setClasses(new ArrayList<ClassEntity>());

        requestDTO = new CourseRequestDTO();
        requestDTO.setName("Java");
        requestDTO.setCoordinatorId(1L);

        responseDTO = new CourseResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Java");
        responseDTO.setCoordinatorId(1L);
    }

    @Test
    @DisplayName("Should create course and return response dto")
    void shouldCreateCourse() {
        when(courseMapper.toEntity(requestDTO)).thenReturn(course);
        when(courseRepo.save(course)).thenReturn(course);
        when(courseMapper.toResponseDTO(course)).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java", result.getName());

        verify(courseMapper).toEntity(requestDTO);
        verify(courseRepo).save(course);
        verify(courseMapper).toResponseDTO(course);
    }

    @Test
    @DisplayName("Should find course by id and return response dto")
    void shouldFindCourseById() {
        when(courseRepo.findById(1L)).thenReturn(course);
        when(courseMapper.toResponseDTO(course)).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java", result.getName());

        verify(courseRepo).findById(1L);
        verify(courseMapper).toResponseDTO(course);
    }

    @Test
    @DisplayName("Should update course and return response dto")
    void shouldUpdateCourse() {
        CourseRequestDTO updatedRequest = new CourseRequestDTO();
        updatedRequest.setName("Java Avançado");
        updatedRequest.setCoordinatorId(1L);

        Course updatedEntity = new Course();
        updatedEntity.setId(1L);
        updatedEntity.setName("Java Avançado");
        updatedEntity.setCoordinator(coordinator);
        updatedEntity.setClasses(new ArrayList<ClassEntity>());

        CourseResponseDTO updatedResponse = new CourseResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Java Avançado");
        updatedResponse.setCoordinatorId(1L);

        when(courseRepo.findById(1L)).thenReturn(course);
        when(courseMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
        when(courseRepo.save(any(Course.class))).thenReturn(updatedEntity);
        when(courseMapper.toResponseDTO(updatedEntity)).thenReturn(updatedResponse);

        CourseResponseDTO result = courseService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals("Java Avançado", result.getName());

        verify(courseRepo).findById(1L);
        verify(courseMapper).toEntity(updatedRequest);
        verify(courseRepo).save(any(Course.class));
        verify(courseMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should delete course")
    void shouldDeleteCourse() {
        doNothing().when(courseRepo).deleteById(1L);

        assertDoesNotThrow(() -> courseService.delete(1L));

        verify(courseRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> courseService.create(null));
    }
}