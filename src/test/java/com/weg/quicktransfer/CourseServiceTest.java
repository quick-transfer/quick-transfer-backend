package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.course.CourseFilter;
import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.dto.course.CourseUpdateRequestDTO;
import com.weg.quicktransfer.exception.CoordinatorNotFoundException;
import com.weg.quicktransfer.exception.CourseNotFoundException;
import com.weg.quicktransfer.mapper.CourseMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.CourseRepository;
import com.weg.quicktransfer.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CoordinatorRepository coordinatorRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseRequestDTO requestDTO;
    private CourseResponseDTO responseDTO;
    private Coordinator coordinator;

    private static final UUID COURSE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID COORDINATOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174111");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @BeforeEach
    void setUp() {
        coordinator = new Coordinator();
        coordinator.setId(COORDINATOR_ID);
        coordinator.setName("Jorge");
        coordinator.setUsername("Jorge_melo123");
        coordinator.setEmail("jorge@gmail.com");
        coordinator.setPassword("Br123456780**");

        ClassEntity classEntity = new ClassEntity();
        classEntity.setAcronym("MI-79");

        List<ClassEntity> classEntities = new ArrayList<>();
        classEntities.add(classEntity);

        course = new Course();
        course.setId(COURSE_ID);
        course.setName("Java");
        course.setCoordinator(coordinator);
        course.setClasses(classEntities);

        requestDTO = new CourseRequestDTO("Java", COORDINATOR_ID);
        responseDTO = new CourseResponseDTO(COURSE_ID, "Java", "Jorge", "jorge@gmail.com");
    }

    @Test
    @DisplayName("Should create course and return response dto")
    void shouldCreateCourse() {
        when(coordinatorRepository.findById(COORDINATOR_ID)).thenReturn(Optional.of(coordinator));
        when(courseMapper.toEntity(requestDTO, coordinator)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.create(requestDTO);

        assertNotNull(result);
        assertEquals(COURSE_ID, result.id());
        assertEquals("Java", result.courseName());

        verify(coordinatorRepository).findById(COORDINATOR_ID);
        verify(courseMapper).toEntity(requestDTO, coordinator);
        verify(courseRepository).save(course);
        verify(courseMapper).toResponse(course);
    }

    @Test
    @DisplayName("Should throw CoordinatorNotFoundException when coordinator is not found on create")
    void shouldThrowExceptionWhenCoordinatorNotFoundOnCreate() {
        when(coordinatorRepository.findById(COORDINATOR_ID)).thenReturn(Optional.empty());

        assertThrows(CoordinatorNotFoundException.class, () -> courseService.create(requestDTO));

        verify(coordinatorRepository).findById(COORDINATOR_ID);
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find course by id and return response dto")
    void shouldFindCourseById() {
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(courseMapper.toResponse(course)).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.findById(COURSE_ID);

        assertNotNull(result);
        assertEquals(COURSE_ID, result.id());
        assertEquals("Java", result.courseName());

        verify(courseRepository).findById(COURSE_ID);
        verify(courseMapper).toResponse(course);
    }

    @Test
    @DisplayName("Should throw CourseNotFoundException when course is not found by id")
    void shouldThrowExceptionWhenCourseNotFoundById() {
        when(courseRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> courseService.findById(NON_EXISTENT_ID));

        verify(courseRepository).findById(NON_EXISTENT_ID);
        verify(courseMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should return list of all courses")
    void shouldFindAllCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(courseMapper.toResponse(course)).thenReturn(responseDTO);

        List<CourseResponseDTO> result = courseService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).courseName());

        verify(courseRepository).findAll();
        verify(courseMapper).toResponse(course);
    }

    @Test
    @DisplayName("Should find course by name")
    void shouldFindCourseByName() {
        // 1. Mock do repository retornando uma lista com o curso
        when(courseRepository.findByNameContaining("Java")).thenReturn(List.of(course));
        when(courseMapper.toResponse(course)).thenReturn(responseDTO);

        // 2. Chamada do serviço recebendo uma Lista
        List<CourseResponseDTO> result = courseService.findByName("Java");

        // 3. Validações na lista retornada
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));

        // 4. Verificação das chamadas corretas
        verify(courseRepository).findByNameContaining("Java");
        verify(courseMapper).toResponse(course);
    }

    @Test
    @DisplayName("Should search courses using filter specification")
    @SuppressWarnings("unchecked")
    void shouldSearchCoursesWithFilter() {
        CourseFilter filter = new CourseFilter("Java", coordinator.getName());

        when(courseRepository.findAll(any(Specification.class))).thenReturn(List.of(course));
        when(courseMapper.toResponse(course)).thenReturn(responseDTO);

        List<CourseResponseDTO> result = courseService.searchCourses(filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(courseRepository).findAll(any(Specification.class));
        verify(courseMapper).toResponse(course);
    }

    @Test
    @DisplayName("Should update course and return response dto")
    void shouldUpdateCourse() {
        CourseUpdateRequestDTO updatedRequest = new CourseUpdateRequestDTO("Java Avançado", COORDINATOR_ID);
        CourseResponseDTO updatedResponse = new CourseResponseDTO(COURSE_ID, "Java Avançado", "Jorge", "jorge@gmail.com");

        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(coordinatorRepository.findById(COORDINATOR_ID)).thenReturn(Optional.of(coordinator));
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(updatedResponse);

        CourseResponseDTO result = courseService.update(COURSE_ID, updatedRequest);

        assertNotNull(result);
        assertEquals("Java Avançado", result.courseName());
        assertEquals("Java Avançado", course.getName());

        verify(courseRepository).findById(COURSE_ID);
        verify(coordinatorRepository).findById(COORDINATOR_ID);
        verify(courseRepository).save(course);
        verify(courseMapper).toResponse(course);
    }

    @Test
    @DisplayName("Should delete course")
    void shouldDeleteCourse() {
        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(COURSE_ID);

        assertDoesNotThrow(() -> courseService.delete(COURSE_ID));

        verify(courseRepository).existsById(COURSE_ID);
        verify(courseRepository).deleteById(COURSE_ID);
    }

    @Test
    @DisplayName("Should throw exception when course to delete does not exist")
    void shouldThrowExceptionWhenDeletingNonExistingCourse() {
        when(courseRepository.existsById(NON_EXISTENT_ID)).thenReturn(false);

        assertThrows(CourseNotFoundException.class, () -> courseService.delete(NON_EXISTENT_ID));

        verify(courseRepository).existsById(NON_EXISTENT_ID);
        verify(courseRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw NullPointerException when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(NullPointerException.class, () -> courseService.create(null));
    }
}