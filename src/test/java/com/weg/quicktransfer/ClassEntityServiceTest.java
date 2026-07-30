package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityUpdateRequestDTO;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.exception.ClassEntityNotFoundException;
import com.weg.quicktransfer.exception.CourseNotFoundException;
import com.weg.quicktransfer.mapper.ClassEntityMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.ClassEntityRepository;
import com.weg.quicktransfer.repo.CourseRepository;
import com.weg.quicktransfer.service.ClassEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassEntityServiceTest {

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ClassEntityMapper classEntityMapper;

    @InjectMocks
    private ClassEntityService classEntityService;

    private ClassEntity classEntity;
    private ClassEntityRequestDTO requestDTO;
    private ClassEntityResponseDTO responseDTO;
    private Course course;

    private static final UUID CLASS_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID COURSE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private static final UUID NON_EXISTENT_COURSE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");
    private static final UUID NON_EXISTENT_CLASS_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @BeforeEach
    void setUp() {
        Coordinator coordinator = new Coordinator();
        coordinator.setName("Bruno");
        coordinator.setUsername("bruno_athanazio");
        coordinator.setEmail("brunoathanazio@gmail.com");
        coordinator.setPassword("Super_securedPassword123");
        coordinator.setRole(Role.COORDINATOR);

        course = new Course();
        course.setId(COURSE_ID);
        course.setName("Java");
        course.setCoordinator(coordinator);
        course.setClasses(new ArrayList<>());

        classEntity = new ClassEntity();
        classEntity.setId(CLASS_ID);
        classEntity.setCourse(course);
        classEntity.setStartDate(LocalDate.now());
        classEntity.setFinishDate(LocalDate.of(2027, 11, 8));
        classEntity.setStudents(new ArrayList<Student>());
        classEntity.setAcronym("MI-79");

        // Request DTO com status e shiftClass
        requestDTO = new ClassEntityRequestDTO(
                COURSE_ID,
                LocalDate.now(),
                LocalDate.of(2027, 11, 8),
                "ON_GOING",
                "MORNING",
                "MI-79"
        );

        // Response DTO atualizado para incluir status e shiftClass
        responseDTO = new ClassEntityResponseDTO(
                CLASS_ID,
                "Java",
                LocalDate.now(),
                LocalDate.of(2027, 11, 8),
                "ON_GOING",
                "MORNING",
                "MI-79"
        );
    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create class entity and return response DTO")
        void shouldCreateClassEntity() {
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
            when(classEntityMapper.toEntity(requestDTO, course)).thenReturn(classEntity);
            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);

            ClassEntityResponseDTO result = classEntityService.create(requestDTO);

            assertNotNull(result);
            assertEquals(CLASS_ID, result.id());
            assertEquals("MI-79", result.acronym());

            verify(courseRepository).findById(COURSE_ID);
            verify(classEntityMapper).toEntity(requestDTO, course);
            verify(classEntityRepository).save(classEntity);
            verify(classEntityMapper).toResponse(classEntity);
        }

        @Test
        @DisplayName("Should throw CourseNotFoundException when course is not found during creation")
        void shouldThrowExceptionWhenCourseNotFoundOnCreate() {
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

            assertThrows(CourseNotFoundException.class, () -> classEntityService.create(requestDTO));

            verify(courseRepository).findById(COURSE_ID);
            verifyNoInteractions(classEntityMapper);
            verify(classEntityRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Find Tests")
    class FindTests {

        @Test
        @DisplayName("Should find class entity by id and return response DTO")
        void shouldFindClassEntityById() {
            when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);

            ClassEntityResponseDTO result = classEntityService.findById(CLASS_ID);

            assertNotNull(result);
            assertEquals(CLASS_ID, result.id());
            assertEquals("MI-79", result.acronym());

            verify(classEntityRepository).findById(CLASS_ID);
            verify(classEntityMapper).toResponse(classEntity);
        }

        @Test
        @DisplayName("Should throw ClassEntityNotFoundException when id is not found")
        void shouldThrowExceptionWhenFindByIdNotFound() {
            when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.empty());

            assertThrows(ClassEntityNotFoundException.class, () -> classEntityService.findById(CLASS_ID));

            verify(classEntityRepository).findById(CLASS_ID);
            verifyNoInteractions(classEntityMapper);
        }

        @Test
        @DisplayName("Should return list of all class entities")
        void shouldFindAllClassEntities() {
            when(classEntityRepository.findAll()).thenReturn(List.of(classEntity));
            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);

            List<ClassEntityResponseDTO> result = classEntityService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("MI-79", result.get(0).acronym());

            verify(classEntityRepository).findAll();
            verify(classEntityMapper).toResponse(classEntity);
        }

        @Test
        @DisplayName("Should return empty list when no class entities exist")
        void shouldReturnEmptyListWhenNoEntitiesExist() {
            when(classEntityRepository.findAll()).thenReturn(List.of());

            List<ClassEntityResponseDTO> result = classEntityService.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(classEntityRepository).findAll();
            verifyNoInteractions(classEntityMapper);
        }
    }

    @Nested
    @DisplayName("Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update class entity fields selectively and return response DTO")
        void shouldUpdateClassEntity() {
            ClassEntityUpdateRequestDTO updateRequest = new ClassEntityUpdateRequestDTO(
                    COURSE_ID,
                    LocalDate.now(),
                    LocalDate.of(2027, 11, 8),
                    "ON_GOING",
                    "MORNING",
                    "MI-79"
            );

            ClassEntityResponseDTO updatedResponse = new ClassEntityResponseDTO(
                    CLASS_ID,
                    "JAVA02",
                    LocalDate.now(),
                    LocalDate.now().plusDays(30),
                    "ON_GOING",
                    "MORNING",
                    "MI-79"
            );

            when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
            when(classEntityMapper.toResponse(classEntity)).thenReturn(updatedResponse);

            ClassEntityResponseDTO result = classEntityService.update(CLASS_ID, updateRequest);

            assertNotNull(result);
            assertEquals("MI-79", result.acronym());

            // Verify entity state updated in-place
            assertEquals("JAVA02", result.courseName());
            assertEquals(LocalDate.now().plusDays(30), result.finishDate());

            verify(classEntityRepository).findById(CLASS_ID);
            verify(courseRepository).findById(COURSE_ID);
            verify(classEntityRepository).save(classEntity);
            verify(classEntityMapper).toResponse(classEntity);
        }

        @Test
        @DisplayName("Should throw ClassEntityNotFoundException when updating non-existent class entity")
        void shouldThrowExceptionWhenUpdateNotFound() {
            ClassEntityUpdateRequestDTO updateRequest = new ClassEntityUpdateRequestDTO(
                    COURSE_ID,
                    LocalDate.now(),
                    LocalDate.of(2027, 11, 8),
                    "ON_GOING",
                    "MORNING",
                    "MI-79"
            );

            // 1. Stub do ID inexistente
            when(classEntityRepository.findById(NON_EXISTENT_CLASS_ID)).thenReturn(Optional.empty());

            // 2. Chamada da Service passando o MESMO ID inexistente
            assertThrows(ClassEntityNotFoundException.class,
                    () -> classEntityService.update(NON_EXISTENT_CLASS_ID, updateRequest));

            // 3. Verificação no repositório com o ID inexistente
            verify(classEntityRepository).findById(NON_EXISTENT_CLASS_ID);
            verify(classEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw CourseNotFoundException when updating with non-existent courseId")
        void shouldThrowExceptionWhenCourseNotFoundOnUpdate() {
            ClassEntityUpdateRequestDTO updateRequest = new ClassEntityUpdateRequestDTO(
                    NON_EXISTENT_COURSE_ID,
                    LocalDate.now(),
                    LocalDate.of(2027, 11, 8),
                    "ON_GOING",
                    "MORNING",
                    "MI-79"
            );


            when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
            when(courseRepository.findById(NON_EXISTENT_COURSE_ID)).thenReturn(Optional.empty());

            assertThrows(CourseNotFoundException.class, () -> classEntityService.update(CLASS_ID, updateRequest));

            verify(classEntityRepository).findById(CLASS_ID);
            verify(courseRepository).findById(NON_EXISTENT_COURSE_ID);
            verify(classEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update only provided fields when request has null properties")
        void shouldUpdateOnlyProvidedFields() {
            ClassEntityUpdateRequestDTO partialRequest = new ClassEntityUpdateRequestDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    "MI-78"
            );


            LocalDate originalStartDate = classEntity.getStartDate();
            LocalDate originalFinishDate = classEntity.getFinishDate();
            Course originalCourse = classEntity.getCourse();

            when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);

            classEntityService.update(CLASS_ID, partialRequest);

            assertEquals("MI-78", classEntity.getAcronym());
            assertEquals(originalStartDate, classEntity.getStartDate());
            assertEquals(originalFinishDate, classEntity.getFinishDate());
            assertEquals(originalCourse, classEntity.getCourse());

            verify(courseRepository, never()).findById(any(UUID.class));
        }

        @Test
        @DisplayName("Should not update acronym when provided string is blank")
        void shouldNotUpdateAcronymWhenBlank() {
            ClassEntityUpdateRequestDTO blankAcronymRequest = new ClassEntityUpdateRequestDTO(
                null,
                null,
                null,
                null,
                null,
                "    "
            );

            when(classEntityRepository.findById(CLASS_ID)).thenReturn(Optional.of(classEntity));
            when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
            when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);

            classEntityService.update(CLASS_ID, blankAcronymRequest);

            assertEquals("MI-79", classEntity.getAcronym());
        }
    }

    @Nested
    @DisplayName("Delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete class entity when it exists")
        void shouldDeleteClassEntity() {
            when(classEntityRepository.existsById(CLASS_ID)).thenReturn(true);
            doNothing().when(classEntityRepository).deleteById(CLASS_ID);

            assertDoesNotThrow(() -> classEntityService.delete(CLASS_ID));

            verify(classEntityRepository).existsById(CLASS_ID);
            verify(classEntityRepository).deleteById(CLASS_ID);
        }

        @Test
        @DisplayName("Should throw ClassEntityNotFoundException when deleting non-existent class entity")
        void shouldThrowExceptionWhenDeleteNotFound() {
            when(classEntityRepository.existsById(CLASS_ID)).thenReturn(false);

            assertThrows(ClassEntityNotFoundException.class, () -> classEntityService.delete(CLASS_ID));

            verify(classEntityRepository).existsById(CLASS_ID);
            verify(classEntityRepository, never()).deleteById(any(UUID.class));
        }
    }
}