package com.weg.quicktransfer;

import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Course;
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

    private Course course;
    private Coordinator coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new Coordinator();

        course = new Course(
                "Java",
                coordinator,
                new ArrayList<ClassEntity>()
        );

        course.setId(1L);
    }

    @Test
    @DisplayName("Should create course")
    void shouldCreateCourse() {
        when(courseRepo.create(any(Course.class)))
                .thenReturn(course);

        Course result = courseRepo.create(course);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java", result.getName());

        verify(courseRepo, times(1))
                .create(course);
    }

    @Test
    @DisplayName("Should find course by id")
    void shouldFindCourseById() {
        when(courseRepo.findById(1L))
                .thenReturn(course);

        Course result = courseRepo.findById(1L);

        assertNotNull(result);
        assertEquals(course.getId(), result.getId());
        assertEquals(course.getName(), result.getName());

        verify(courseRepo, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("Should update course")
    void shouldUpdateCourse() {
        Course updatedCourse = new Course(
                "Java Avançado",
                coordinator,
                new ArrayList<ClassEntity>()
        );
        updatedCourse.setId(1L);

        when(courseRepo.findById(1L))
                .thenReturn(course);

        when(courseRepo.create(any(Course.class)))
                .thenReturn(updatedCourse);

        Course result = courseRepo.update(1L, updatedCourse);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java Avançado", result.getName());

        verify(courseRepo, times(1))
                .findById(1L);

        verify(courseRepo, times(1))
                .create(any(Course.class));
    }

    @Test
    @DisplayName("Should delete course")
    void shouldDeleteCourse() {
        when(courseRepo.findById(1L))
                .thenReturn(course);

        doNothing().when(courseRepo)
                .deleteById(1L);

        assertDoesNotThrow(() -> courseRepo.delete(1L));

        verify(courseRepo, times(1))
                .deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        Course invalidCourse = new Course(
                null,
                coordinator,
                new ArrayList<ClassEntity>()
        );

        assertThrows(IllegalArgumentException.class,
                () -> courseRepo.create(invalidCourse));
    }

    @Test
    @DisplayName("Should add class to course")
    void shouldAddClassToCourse() {
        ClassEntity classEntity = new ClassEntity();

        course.addClass(classEntity);

        assertTrue(course.getClasses().contains(classEntity));
    }

    @Test
    @DisplayName("Should remove class from course")
    void shouldRemoveClassFromCourse() {
        ClassEntity classEntity = new ClassEntity();

        course.addClass(classEntity);
        course.removeClass(classEntity);

        assertFalse(course.getClasses().contains(classEntity));
    }
}