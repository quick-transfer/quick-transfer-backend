package com.weg.quicktransfer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repository.VacancyRepository;
import com.weg.quicktransfer.exception.BusinessRuleException;
import com.weg.quicktransfer.service.VacancyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ManagerVacancyRulesTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @InjectMocks
    private VacancyService vacancyService;

    @Captor
    private ArgumentCaptor<Vacancy> vacancyCaptor;

    @Test
    public void vacancyCreationValidDataShouldSaveSuccessfully() {
        Vacancy vacancy = new Vacancy();
        vacancy.setName("Junior Developer");
        vacancy.setShift(Shift.FIRST);
        vacancy.setAvailableSpots(3);

        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);

        Vacancy savedVacancy = vacancyService.createVacancy(vacancy);

        verify(vacancyRepository).save(vacancyCaptor.capture());
        Vacancy captured = vacancyCaptor.getValue();

        assertEquals("Junior Developer", captured.getName());
        assertEquals(3, captured.getAvailableSpots());
    }

    @Test
    public void filterVacanciesBySectionShouldReturnFilteredList() {
        Section itSection = new Section();
        itSection.setName("IT");

        Vacancy v1 = new Vacancy(); v1.setSection(itSection);
        Vacancy v2 = new Vacancy(); v2.setSection(itSection);

        when(vacancyRepository.findBySectionName("IT")).thenReturn(Arrays.asList(v1, v2));

        List<Vacancy> result = vacancyService.searchVacancies(null, "IT", null, null);

        assertEquals(2, result.size());
        verify(vacancyRepository).findBySectionName("IT");
    }

    @Test
    @DisplayName("Should not allow closing a vacancy without reaching the minimum number of candidates")
    public void closeVacancyMinimumAmountNotMetshouldThrowException() {

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setAvailableSpots(2);
        vacancy.setAppliedStudentsCount(1);

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        assertThrows(BusinessRuleException.class, () -> {
            vacancyService.closeVacancy(1L);
        });
    }

    @Test
    public void useVacancyTemplateShouldCreateNewVacancyWithoutOriginalAmount() {

        Vacancy template = new Vacancy();
        template.setName("Standard Template");
        template.setShift(Shift.SECOND);
        template.setAvailableSpots(10);

        when(vacancyRepository.findById(99L)).thenReturn(Optional.of(template));

        Vacancy newVacancy = vacancyService.useTemplate(99L, 5);

        assertEquals("Standard Template", newVacancy.getName());
        assertEquals(Shift.SECOND, newVacancy.getShift());
        assertEquals(5, newVacancy.getAvailableSpots(), "Should assume the new amount entered by the manager");
        assertNull(newVacancy.getId(), "Should be a new entity");
    }
}