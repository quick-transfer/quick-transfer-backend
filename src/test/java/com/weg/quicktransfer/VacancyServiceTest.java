package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.mapper.VacancyMapper;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.service.VacancyService;
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
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepo;

    @Mock
    private VacancyMapper vacancyMapper;

    @InjectMocks
    private VacancyService vacancyService;

    private Vacancy vacancy;
    private VacancyRequestDTO requestDTO;
    private VacancyResponseDTO responseDTO;
    private Place place;

    @BeforeEach
    void setUp() {
        place = new Place();
        place.setId(1L);

        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setShift(Shift.FIRST);
        vacancy.setPlace(place);
        vacancy.setInterviews(new ArrayList<Interview>());

        requestDTO = new VacancyRequestDTO();
        requestDTO.setShift(Shift.FIRST);
        requestDTO.setPlaceId(1L);

        responseDTO = new VacancyResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setShift(Shift.FIRST);
        responseDTO.setPlaceId(1L);
    }

    @Test
    @DisplayName("Should create vacancy and return response dto")
    void shouldCreateVacancy() {
        when(vacancyMapper.toEntity(requestDTO)).thenReturn(vacancy);
        when(vacancyRepo.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toResponseDTO(vacancy)).thenReturn(responseDTO);

        VacancyResponseDTO result = vacancyService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Shift.FIRST, result.getShift());
        assertEquals(1L, result.getPlaceId());

        verify(vacancyMapper).toEntity(requestDTO);
        verify(vacancyRepo).save(vacancy);
        verify(vacancyMapper).toResponseDTO(vacancy);
    }

    @Test
    @DisplayName("Should find vacancy by id and return response dto")
    void shouldFindVacancyById() {
        when(vacancyRepo.findById(1L)).thenReturn(vacancy);
        when(vacancyMapper.toResponseDTO(vacancy)).thenReturn(responseDTO);

        VacancyResponseDTO result = vacancyService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Shift.FIRST, result.getShift());

        verify(vacancyRepo).findById(1L);
        verify(vacancyMapper).toResponseDTO(vacancy);
    }

    @Test
    @DisplayName("Should update vacancy and return response dto")
    void shouldUpdateVacancy() {
        VacancyRequestDTO updatedRequest = new VacancyRequestDTO();
        updatedRequest.setShift(Shift.SECOND);
        updatedRequest.setPlaceId(1L);

        Vacancy updatedEntity = new Vacancy();
        updatedEntity.setId(1L);
        updatedEntity.setShift(Shift.SECOND);
        updatedEntity.setPlace(place);
        updatedEntity.setInterviews(new ArrayList<Interview>());

        VacancyResponseDTO updatedResponse = new VacancyResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setShift(Shift.SECOND);
        updatedResponse.setPlaceId(1L);

        when(vacancyRepo.findById(1L)).thenReturn(vacancy);
        when(vacancyMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
        when(vacancyRepo.save(any(Vacancy.class))).thenReturn(updatedEntity);
        when(vacancyMapper.toResponseDTO(updatedEntity)).thenReturn(updatedResponse);

        VacancyResponseDTO result = vacancyService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Shift.SEGUNDO, result.getShift());

        verify(vacancyRepo).findById(1L);
        verify(vacancyMapper).toEntity(updatedRequest);
        verify(vacancyRepo).save(any(Vacancy.class));
        verify(vacancyMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should delete vacancy")
    void shouldDeleteVacancy() {
        doNothing().when(vacancyRepo).deleteById(1L);

        assertDoesNotThrow(() -> vacancyService.delete(1L));

        verify(vacancyRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> vacancyService.create(null));
    }
}