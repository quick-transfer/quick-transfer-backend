//package com.weg.quicktransfer;
//
//import com.weg.quicktransfer.dto.CourseRequestDTO;
//import com.weg.quicktransfer.dto.CourseResponseDTO;
//import com.weg.quicktransfer.mapper.CourseMapper;
//import com.weg.quicktransfer.model.Coordinator;
//import com.weg.quicktransfer.model.Course;
//import com.weg.quicktransfer.model.ClassEntity;
//import com.weg.quicktransfer.repo.CourseRepository;
//import com.weg.quicktransfer.service.CourseService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CourseServiceTest {
//
//    @Mock
//    private CourseRepository courseRepository;
//
//    @Mock
//    private CourseMapper courseMapper;
//
//    @InjectMocks
//    private CourseService courseService;
//
//    private Course course;
//    private CourseRequestDTO requestDTO;
//    private CourseResponseDTO responseDTO;
//    private Coordinator coordinator;
//
//    @BeforeEach
//    void setUp() {
//        coordinator = new Coordinator();
//        coordinator.setId(1L);
//
//        course = new Course();
//        course.setId(1L);
//        course.setName("Java");
//        course.setCoordinator(coordinator);
//        course.setClasses(new ArrayList<ClassEntity>());
//
//        // Inicialização usando os construtores canônicos dos Records
//        requestDTO = new CourseRequestDTO("Java", 1L);
//        responseDTO = new CourseResponseDTO(1L, "Java", 1L);
//    }
//
//    @Test
//    @DisplayName("Should create course and return response dto")
//    void shouldCreateCourse() {
//        when(courseMapper.toEntity(requestDTO)).thenReturn(course);
//        when(courseRepository.save(course)).thenReturn(course);
//        when(courseMapper.toResponse(course)).thenReturn(responseDTO);
//
//        CourseResponseDTO result = courseService.create(requestDTO);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id()); // Acesso ao componente id() do record
//        assertEquals("Java", result.name()); // Acesso ao componente name() do record
//
//        verify(courseMapper).toEntity(requestDTO);
//        verify(courseRepository).save(course);
//        verify(courseMapper).toResponse(course);
//    }
//
//    @Test
//    @DisplayName("Should find course by id and return response dto")
//    void shouldFindCourseById() {
//        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
//        when(courseMapper.toResponse(course)).thenReturn(responseDTO);
//
//        CourseResponseDTO result = courseService.findById(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id());
//        assertEquals("Java", result.name());
//
//        verify(courseRepository).findById(1L);
//        verify(courseMapper).toResponse(course);
//    }
//
//    @Test
//    @DisplayName("Should update course and return response dto")
//    void shouldUpdateCourse() {
//        // Records são imutáveis; criamos novas instâncias para representar dados modificados
//        CourseRequestDTO updatedRequest = new CourseRequestDTO("Java Avançado", 1L);
//
//        Course updatedEntity = new Course();
//        updatedEntity.setId(1L);
//        updatedEntity.setName("Java Avançado");
//        updatedEntity.setCoordinator(coordinator);
//        updatedEntity.setClasses(new ArrayList<ClassEntity>());
//
//        CourseResponseDTO updatedResponse = new CourseResponseDTO(1L, "Java Avançado", 1L);
//
//        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
//        when(courseMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
//        when(courseRepository.save(any(Course.class))).thenReturn(updatedEntity);
//        when(courseMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);
//
//        CourseResponseDTO result = courseService.update(1L, updatedRequest);
//
//        assertNotNull(result);
//        assertEquals("Java Avançado", result.name());
//
//        verify(courseRepository).findById(1L);
//        verify(courseMapper).toEntity(updatedRequest);
//        verify(courseRepository).save(any(Course.class));
//        verify(courseMapper).toResponse(updatedEntity);
//    }
//
//    @Test
//    @DisplayName("Should delete course")
//    void shouldDeleteCourse() {
//        doNothing().when(courseRepository).deleteById(1L);
//
//        assertDoesNotThrow(() -> courseService.delete(1L));
//
//        verify(courseRepository).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("Should throw exception when request dto is null")
//    void shouldThrowExceptionWhenRequestDtoIsNull() {
//        assertThrows(IllegalArgumentException.class, () -> courseService.create(null));
//    }
//}