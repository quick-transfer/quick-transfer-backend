//package com.weg.quicktransfer;
//
//import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
//import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
//import com.weg.quicktransfer.dto.classEntity.ClassEntityUpdateRequestDTO;
//import com.weg.quicktransfer.enums.Role;
//import com.weg.quicktransfer.exception.ClassEntityNotFoundException;
//import com.weg.quicktransfer.exception.CourseNotFoundException;
//import com.weg.quicktransfer.mapper.ClassEntityMapper;
//import com.weg.quicktransfer.model.ClassEntity;
//import com.weg.quicktransfer.model.Coordinator;
//import com.weg.quicktransfer.model.Course;
//import com.weg.quicktransfer.model.Student;
//import com.weg.quicktransfer.repo.ClassEntityRepository;
//import com.weg.quicktransfer.repo.CourseRepository;
//import com.weg.quicktransfer.service.ClassEntityService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ClassEntityServiceTest {
//
//    @Mock
//    private ClassEntityRepository classEntityRepository;
//
//    @Mock
//    private CourseRepository courseRepository;
//
//    @Mock
//    private ClassEntityMapper classEntityMapper;
//
//    @InjectMocks
//    private ClassEntityService classEntityService;
//
//    private ClassEntity classEntity;
//    private ClassEntityRequestDTO requestDTO;
//    private ClassEntityResponseDTO responseDTO;
//    private Course course;
//
//    @BeforeEach
//    void setUp() {
//        Coordinator coordinator = new Coordinator();
//        coordinator.setName("Bruno");
//        coordinator.setUsername("bruno_athanazio");
//        coordinator.setEmail("brunoathanazio@gmail.com");
//        coordinator.setPassword("Super_securedPassword123");
//        coordinator.setRole(Role.COORDINATOR);
//
//        course = new Course();
//        course.setId(1L);
//        course.setName("Java");
//        course.setCoordinator(coordinator);
//        course.setClasses(new ArrayList<>());
//
//        classEntity = new ClassEntity();
//        classEntity.setId(1L);
//        classEntity.setCourse(course);
//        classEntity.setStartDate(LocalDate.now());
//        classEntity.setFinishDate(LocalDate.of(2027, 11, 8));
//        classEntity.setStudents(new ArrayList<Student>());
//        classEntity.setAcronym("MI-79");
//
//        requestDTO = new ClassEntityRequestDTO(1L, LocalDate.now(), LocalDate.of(2027, 11, 8), "MI-79");
//        responseDTO = new ClassEntityResponseDTO(1L, "Java", LocalDate.now(), LocalDate.of(2027, 11, 8), "MI-79");
//    }
//
//    @Nested
//    @DisplayName("Create Tests")
//    class CreateTests {
//
//        @Test
//        @DisplayName("Should create class entity and return response DTO")
//        void shouldCreateClassEntity() {
//            when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
//            when(classEntityMapper.toEntity(requestDTO, course)).thenReturn(classEntity);
//            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
//            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);
//
//            ClassEntityResponseDTO result = classEntityService.create(requestDTO);
//
//            assertNotNull(result);
//            assertEquals(1L, result.id());
//            assertEquals("MI-79", result.acronym());
//
//            verify(courseRepository).findById(1L);
//            verify(classEntityMapper).toEntity(requestDTO, course);
//            verify(classEntityRepository).save(classEntity);
//            verify(classEntityMapper).toResponse(classEntity);
//        }
//
//        @Test
//        @DisplayName("Should throw CourseNotFoundException when course is not found during creation")
//        void shouldThrowExceptionWhenCourseNotFoundOnCreate() {
//            when(courseRepository.findById(1L)).thenReturn(Optional.empty());
//
//            assertThrows(CourseNotFoundException.class, () -> classEntityService.create(requestDTO));
//
//            verify(courseRepository).findById(1L);
//            verifyNoInteractions(classEntityMapper);
//            verify(classEntityRepository, never()).save(any());
//        }
//    }
//
//    @Nested
//    @DisplayName("Find Tests")
//    class FindTests {
//
//        @Test
//        @DisplayName("Should find class entity by id and return response DTO")
//        void shouldFindClassEntityById() {
//            when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
//            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);
//
//            ClassEntityResponseDTO result = classEntityService.findById(1L);
//
//            assertNotNull(result);
//            assertEquals(1L, result.id());
//            assertEquals("MI-79", result.acronym());
//
//            verify(classEntityRepository).findById(1L);
//            verify(classEntityMapper).toResponse(classEntity);
//        }
//
//        @Test
//        @DisplayName("Should throw ClassEntityNotFoundException when id is not found")
//        void shouldThrowExceptionWhenFindByIdNotFound() {
//            when(classEntityRepository.findById(1L)).thenReturn(Optional.empty());
//
//            assertThrows(ClassEntityNotFoundException.class, () -> classEntityService.findById(1L));
//
//            verify(classEntityRepository).findById(1L);
//            verifyNoInteractions(classEntityMapper);
//        }
//
//        @Test
//        @DisplayName("Should return list of all class entities")
//        void shouldFindAllClassEntities() {
//            when(classEntityRepository.findAll()).thenReturn(List.of(classEntity));
//            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);
//
//            List<ClassEntityResponseDTO> result = classEntityService.findAll();
//
//            assertNotNull(result);
//            assertEquals(1, result.size());
//            assertEquals("MI-79", result.get(0).acronym());
//
//            verify(classEntityRepository).findAll();
//            verify(classEntityMapper).toResponse(classEntity);
//        }
//
//        @Test
//        @DisplayName("Should return empty list when no class entities exist")
//        void shouldReturnEmptyListWhenNoEntitiesExist() {
//            when(classEntityRepository.findAll()).thenReturn(List.of());
//
//            List<ClassEntityResponseDTO> result = classEntityService.findAll();
//
//            assertNotNull(result);
//            assertTrue(result.isEmpty());
//
//            verify(classEntityRepository).findAll();
//            verifyNoInteractions(classEntityMapper);
//        }
//    }
//
//    @Nested
//    @DisplayName("Update Tests")
//    class UpdateTests {
//
//        @Test
//        @DisplayName("Should update class entity fields selectively and return response DTO")
//        void shouldUpdateClassEntity() {
//            ClassEntityUpdateRequestDTO updateRequest = new ClassEntityUpdateRequestDTO(
//                    1L, LocalDate.now(), LocalDate.now().plusDays(30), "JAVA02"
//            );
//
//            ClassEntityResponseDTO updatedResponse = new ClassEntityResponseDTO(
//                    1L, "Java", LocalDate.now(), LocalDate.now().plusDays(30), "JAVA02"
//            );
//
//            when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
//            when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
//            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
//            when(classEntityMapper.toResponse(classEntity)).thenReturn(updatedResponse);
//
//            ClassEntityResponseDTO result = classEntityService.update(1L, updateRequest);
//
//            assertNotNull(result);
//            assertEquals("JAVA02", result.acronym());
//
//            // Verify entity state updated in-place
//            assertEquals("JAVA02", classEntity.getAcronym());
//            assertEquals(LocalDate.now().plusDays(30), classEntity.getFinishDate());
//
//            verify(classEntityRepository).findById(1L);
//            verify(courseRepository).findById(1L);
//            verify(classEntityRepository).save(classEntity);
//            verify(classEntityMapper).toResponse(classEntity);
//        }
//
//        @Test
//        @DisplayName("Should throw ClassEntityNotFoundException when updating non-existent class entity")
//        void shouldThrowExceptionWhenUpdateNotFound() {
//            ClassEntityUpdateRequestDTO updateRequest = new ClassEntityUpdateRequestDTO(
//                    1L, LocalDate.now(), LocalDate.now().plusDays(30), "JAVA02"
//            );
//
//            when(classEntityRepository.findById(1L)).thenReturn(Optional.empty());
//
//            assertThrows(ClassEntityNotFoundException.class, () -> classEntityService.update(1L, updateRequest));
//
//            verify(classEntityRepository).findById(1L);
//            verify(classEntityRepository, never()).save(any());
//        }
//
//        @Test
//        @DisplayName("Should throw CourseNotFoundException when updating with non-existent courseId")
//        void shouldThrowExceptionWhenCourseNotFoundOnUpdate() {
//            ClassEntityUpdateRequestDTO updateRequest = new ClassEntityUpdateRequestDTO(
//                    99L, null, null, "NOVA-SIGLA"
//            );
//
//            when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
//            when(courseRepository.findById(99L)).thenReturn(Optional.empty());
//
//            assertThrows(CourseNotFoundException.class, () -> classEntityService.update(1L, updateRequest));
//
//            verify(classEntityRepository).findById(1L);
//            verify(courseRepository).findById(99L);
//            verify(classEntityRepository, never()).save(any());
//        }
//
//        @Test
//        @DisplayName("Should update only provided fields when request has null properties")
//        void shouldUpdateOnlyProvidedFields() {
//            // DTO enviando APENAS a sigla (demais campos nulos)
//            ClassEntityUpdateRequestDTO partialRequest = new ClassEntityUpdateRequestDTO(
//                    null, null, null, "NOVA-SIGLA"
//            );
//
//            LocalDate originalStartDate = classEntity.getStartDate();
//            LocalDate originalFinishDate = classEntity.getFinishDate();
//            Course originalCourse = classEntity.getCourse();
//
//            when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
//            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
//            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);
//
//            classEntityService.update(1L, partialRequest);
//
//            // Garante que o acronym mudou, mas as datas e o curso permaneceram intactos
//            assertEquals("NOVA-SIGLA", classEntity.getAcronym());
//            assertEquals(originalStartDate, classEntity.getStartDate());
//            assertEquals(originalFinishDate, classEntity.getFinishDate());
//            assertEquals(originalCourse, classEntity.getCourse());
//
//            // Garante que nem tentou buscar curso no banco já que o courseId veio nulo
//            verify(courseRepository, never()).findById(anyLong());
//        }
//
//        @Test
//        @DisplayName("Should not update acronym when provided string is blank")
//        void shouldNotUpdateAcronymWhenBlank() {
//            ClassEntityUpdateRequestDTO blankAcronymRequest = new ClassEntityUpdateRequestDTO(
//                    null, null, null, "   "
//            );
//
//            when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
//            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
//            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);
//
//            classEntityService.update(1L, blankAcronymRequest);
//
//            // O acronym deve se manter o original ("MI-79") do setUp()
//            assertEquals("MI-79", classEntity.getAcronym());
//        }
//    }
//
//    @Nested
//    @DisplayName("Delete Tests")
//    class DeleteTests {
//
//        @Test
//        @DisplayName("Should delete class entity when it exists")
//        void shouldDeleteClassEntity() {
//            when(classEntityRepository.existsById(1L)).thenReturn(true);
//            doNothing().when(classEntityRepository).deleteById(1L);
//
//            assertDoesNotThrow(() -> classEntityService.delete(1L));
//
//            verify(classEntityRepository).existsById(1L);
//            verify(classEntityRepository).deleteById(1L);
//        }
//
//        @Test
//        @DisplayName("Should throw ClassEntityNotFoundException when deleting non-existent class entity")
//        void shouldThrowExceptionWhenDeleteNotFound() {
//            when(classEntityRepository.existsById(1L)).thenReturn(false);
//
//            assertThrows(ClassEntityNotFoundException.class, () -> classEntityService.delete(1L));
//
//            verify(classEntityRepository).existsById(1L);
//            verify(classEntityRepository, never()).deleteById(anyLong());
//        }
//    }
//}