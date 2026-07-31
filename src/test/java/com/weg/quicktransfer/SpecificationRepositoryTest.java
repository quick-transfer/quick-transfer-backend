package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.classEntity.ClassEntityFilter;
import com.weg.quicktransfer.dto.vacancy.VacancyFilter;
import com.weg.quicktransfer.config.CacheConfig;
import com.weg.quicktransfer.enums.*;
import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.*;
import com.weg.quicktransfer.repo.specifications.ClassEntitySpecification;
import com.weg.quicktransfer.repo.specifications.VacancySpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Import(CacheConfig.class)
class SpecificationRepositoryTest {

    @Autowired
    private CoordinatorRepository coordinatorRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ClassEntityRepository classEntityRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private VacancyRepository vacancyRepository;
    @Autowired
    private VacancySkillRepository vacancySkillRepository;

    @BeforeEach
    void setUp() {
        Coordinator coordinator = coordinatorRepository.save(new Coordinator(
                "Coordinator", "coordinator01", "coordinator@example.com", "encoded-password"));
        Course course = courseRepository.save(new Course("Software", coordinator));
        classEntityRepository.save(new ClassEntity(
                course,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(6),
                StatusClass.NOT_STARTED,
                ShiftClass.MORNING,
                "DEV-01"));

        Place place = placeRepository.save(new Place("Technology Center", Park.WEG_II, Section.IT));
        Vacancy vacancy = new Vacancy(
                "Java Developer",
                "Backend development",
                2L,
                Area.IT,
                Shift.FIRST,
                place);
        VacancySkill vacancySkill = vacancySkillRepository.save(
                new VacancySkill("Java", SkillType.TECHNICAL, 7.0));
        vacancy.addSkill(vacancySkill);
        vacancyRepository.save(vacancy);
    }

    @Test
    void shouldFilterClassByShiftClassAttribute() {
        ClassEntityFilter filter = new ClassEntityFilter(
                null, null, null, null, ShiftClass.MORNING, null);

        List<ClassEntity> result = classEntityRepository.findAll(
                ClassEntitySpecification.getFilteredClassEntities(filter));

        assertEquals(1, result.size());
        assertEquals(ShiftClass.MORNING, result.get(0).getShiftClass());
    }

    @Test
    void shouldFilterVacancyByNumbersVacanciesAttribute() {
        VacancyFilter filter = new VacancyFilter(null, null, 2L, null, null, null, null);

        List<Vacancy> result = vacancyRepository.findAll(
                VacancySpecification.getFilteredVacancies(filter));

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getNumbersVacancies());
    }

    @Test
    void shouldFilterVacancyBySkillName() {
        VacancyFilter filter = new VacancyFilter(null, null, null, null, null, null, "java");

        List<Vacancy> result = vacancyRepository.findAll(
                VacancySpecification.getFilteredVacancies(filter));

        assertEquals(1, result.size());
        assertEquals("Java Developer", result.get(0).getName());
        assertEquals("Java", result.get(0).getSkills().get(0).getName());
    }
}
