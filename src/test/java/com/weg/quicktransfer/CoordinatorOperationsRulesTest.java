package com.weg.quicktransfer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.CourseRepository;
import com.weg.quicktransfer.repo.ClassEntityRepository;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.exception.BusinessRuleException;
import com.weg.quicktransfer.service.CoordinatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class CoordinatorOperationsRulesTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @InjectMocks
    private CoordinatorService coordinatorService;

    @Test
    @DisplayName("Should create course when complete data is provided")
    public void createCourseCompleteDataShouldSave() {
        Course course = new Course();
        course.setName("Industrial Mechanics");

        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course saved = coordinatorService.createCourse(course);

        assertNotNull(saved);
        verify(courseRepository).save(course);
    }

    @Test
    @DisplayName("Should not allow creating a class with a finish date before the start date")
    public void createClassStartDateGreaterThanFinishDateShouldThrowException() {
        ClassEntity studentClass = new ClassEntity();
        studentClass.setAcronym("MEC-2024");
        studentClass.setFinishDate(LocalDate.of(2024, 1, 1));

        assertThrows(IllegalArgumentException.class, () -> {
            coordinatorService.createClassEntity(studentClass);
        });

        verify(classEntityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not allow applying the same apprentice more than once to the same vacancy")
    public void applyApprenticeToVacancyStudentAlreadyAppliedShouldThrowException() {
        Student student = new Student();
        student.setId(1L);

        Vacancy vacancy = new Vacancy();
        vacancy.setId(100L);

        when(vacancyRepository.findById(100L)).thenReturn(Optional.of(vacancy));

        assertThrows(BusinessRuleException.class, () -> {
            coordinatorService.applyStudentToVacancy(student, 100L);
        });
    }
}