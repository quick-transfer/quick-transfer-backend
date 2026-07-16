package com.weg.quicktransfer;

import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.ClassEntityRepo;
import com.weg.quicktransfer.service.ClassEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassEntityServiceTest {

    @Mock
    private ClassEntityRepo classEntityRepo;

    @InjectMocks
    private ClassEntityService classEntityService;

    private ClassEntity classEntity;
    private Course course;

    @BeforeEach
    void setUp() {

        Coordinator coordinator = new Coordinator();

        course = new Course(
                "Java",
                coordinator,
                new ArrayList<>()
        );

        classEntity = new ClassEntity(
                course,
                LocalDate.now(),
                new ArrayList<Student>(),
                "JAVA01"
        );

        classEntity.setId(1L);
    }

    @Test
    @DisplayName("Should create class")
    void shouldCreateClass() {

        when(classEntityRepo.create(any(ClassEntity.class)))
                .thenReturn(classEntity);

        ClassEntity result = classEntityService.create(classEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("JAVA01", result.getAcronym());

        verify(classEntityRepo, times(1))
                .create(classEntity);
    }

    @Test
    @DisplayName("Should find class by id")
    void shouldFindClassById() {

        when(classEntityRepo.findById(1L))
                .thenReturn(classEntity);

        ClassEntity result = classEntityService.findById(1L);

        assertNotNull(result);
        assertEquals(classEntity.getId(), result.getId());

        verify(classEntityRepo, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("Should update class")
    void shouldUpdateClass() {

        ClassEntity updatedClass = new ClassEntity(
                course,
                LocalDate.now().plusDays(30),
                new ArrayList<>(),
                "JAVA02"
        );

        updatedClass.setId(1L);

        when(classEntityRepo.findById(1L))
                .thenReturn(classEntity);

        when(classEntityRepo.create(any(ClassEntity.class)))
                .thenReturn(updatedClass);

        ClassEntity result =
                classEntityService.update(1L, updatedClass);

        assertNotNull(result);
        assertEquals("JAVA02", result.getAcronym());

        verify(classEntityRepo, times(1))
                .findById(1L);

        verify(classEntityRepo, times(1))
                .create(any(ClassEntity.class));
    }

    @Test
    @DisplayName("Should delete class")
    void shouldDeleteClass() {

        when(classEntityRepo.findById(1L))
                .thenReturn(classEntity);

        doNothing()
                .when(classEntityRepo)
                .deleteById(1L);

        assertDoesNotThrow(() ->
                classEntityService.delete(1L));

        verify(classEntityRepo, times(1))
                .deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when acronym is null")
    void shouldThrowExceptionWhenAcronymIsNull() {

        ClassEntity invalidClass = new ClassEntity(
                course,
                LocalDate.now(),
                new ArrayList<>(),
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> classEntityService.create(invalidClass)
        );
    }

    @Test
    @DisplayName("Should add student to class")
    void shouldAddStudentToClass() {
        Student student = mock(Student.class);

        classEntity.addStudent(student);

        assertTrue(classEntity.getStudents().contains(student));
    }

    @Test
    @DisplayName("Should remove student from class")
    void shouldRemoveStudentFromClass() {
        Student student = mock(Student.class);

        classEntity.addStudent(student);
        classEntity.removeStudent(student);

        assertFalse(classEntity.getStudents().contains(student));
    }
}