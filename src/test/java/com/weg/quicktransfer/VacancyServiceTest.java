package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyUpdateRequestDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.mapper.VacancyMapper;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.PlaceRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private PlaceRepository placeRepository; // 1. Added missing mock

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
        place.setPlaceName("WEG Jaraguá");
        place.setPark(Park.WEG_II);
        place.setSection(Section.TI);

        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Fullstack");
        vacancy.setDescription("description");
        vacancy.setNumbersVacancies(8L);
        vacancy.setArea(Area.TI);
        vacancy.setShift(Shift.FIRST);
        vacancy.setPlace(place);
        vacancy.setInterviews(new ArrayList<Interview>());

        requestDTO = new VacancyRequestDTO("Fullstack", "description", 8L, Area.TI.toString(), Shift.FIRST.toString(), 1L);
        responseDTO = new VacancyResponseDTO(1L, "Fullstack", "description", 8L, Area.TI.toString(), Shift.FIRST.toString(), Park.WEG_II.toString(), Section.TI.toString());
    }

    @Test
    @DisplayName("Should create vacancy and return response dto")
    void shouldCreateVacancy() {
        // Mock PlaceRepository lookup
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(vacancyMapper.toEntity(requestDTO, place)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        VacancyResponseDTO result = vacancyService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(Shift.FIRST.toString(), result.shift());

        verify(placeRepository).findById(1L);
        verify(vacancyMapper).toEntity(requestDTO, place);
        verify(vacancyRepository).save(vacancy);
        verify(vacancyMapper).toResponse(vacancy);
    }

    @Test
    @DisplayName("Should find vacancy by id and return response dto")
    void shouldFindVacancyById() {
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        VacancyResponseDTO result = vacancyService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(Shift.FIRST.toString(), result.shift());

        verify(vacancyRepository).findById(1L);
        verify(vacancyMapper).toResponse(vacancy);
    }

    @Test
    @DisplayName("Should update vacancy and return response dto")
    void shouldUpdateVacancy() {
        // 2. Used VacancyUpdateRequestDTO with matching types (Strings for Enums)
        VacancyUpdateRequestDTO updateRequestDTO = new VacancyUpdateRequestDTO(
                "Fullstack Updated", "new description", 10L, Area.TI.toString(), Shift.SECOND.toString(), 1L
        );

        Vacancy updatedEntity = new Vacancy();
        updatedEntity.setId(1L);
        updatedEntity.setName("Fullstack Updated");
        updatedEntity.setDescription("new description");
        updatedEntity.setNumbersVacancies(10L);
        updatedEntity.setArea(Area.TI);
        updatedEntity.setShift(Shift.SECOND);
        updatedEntity.setPlace(place);

        VacancyResponseDTO updatedResponse = new VacancyResponseDTO(
                1L, "Fullstack Updated", "new description", 10L, Area.TI.toString(), Shift.SECOND.toString(), Park.WEG_II.toString(), Section.TI.toString()
        );

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(updatedEntity);
        when(vacancyMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

        VacancyResponseDTO result = vacancyService.update(1L, updateRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(Shift.SECOND.toString(), result.shift());

        verify(vacancyRepository).findById(1L);
        verify(placeRepository).findById(1L);
        verify(vacancyRepository).save(any(Vacancy.class));
        verify(vacancyMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Should delete vacancy")
    void shouldDeleteVacancy() {
        // 3. Stub existsById to true so exception is not thrown
        when(vacancyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vacancyRepository).deleteById(1L);

        assertDoesNotThrow(() -> vacancyService.delete(1L));

        verify(vacancyRepository).existsById(1L);
        verify(vacancyRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(NullPointerException.class, () -> vacancyService.create(null));
    }
}