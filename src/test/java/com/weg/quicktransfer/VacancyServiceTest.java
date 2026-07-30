package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.vacancy.VacancyFilter;
import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyUpdateRequestDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.exception.VacancyNotFoundException;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private VacancyService vacancyService;

    private Vacancy vacancy;
    private VacancyRequestDTO requestDTO;
    private VacancyResponseDTO responseDTO;
    private Place place;

    private static final UUID VACANCY_ID      = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID PLACE_ID        = UUID.fromString("123e4567-e89b-12d3-a456-426614174333");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @BeforeEach
    void setUp() {
        place = new Place();
        place.setId(PLACE_ID);
        place.setPlaceName("WEG Jaraguá");
        place.setPark(Park.WEG_II);
        place.setSection(Section.IT);

        vacancy = new Vacancy();
        vacancy.setId(VACANCY_ID);
        vacancy.setName("Fullstack");
        vacancy.setDescription("description");
        vacancy.setNumbersVacancies(8L);
        vacancy.setArea(Area.IT);
        vacancy.setShift(Shift.FIRST);
        vacancy.setPlace(place);
        vacancy.setInterviews(new ArrayList<Interview>());

        requestDTO = new VacancyRequestDTO("Fullstack", "description", 8L, Area.IT.toString(), Shift.FIRST.toString(), PLACE_ID);
        responseDTO = new VacancyResponseDTO(VACANCY_ID, "Fullstack", "description", 8L, Area.IT.toString(), Shift.FIRST.toString(), Park.WEG_II.toString(), Section.IT.toString());
    }

    // --- CREATE TESTS ---

    @Test
    @DisplayName("Should create vacancy and return response dto")
    void shouldCreateVacancy() {
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(vacancyMapper.toEntity(requestDTO, place)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        VacancyResponseDTO result = vacancyService.create(requestDTO);

        assertNotNull(result);
        assertEquals(VACANCY_ID, result.id());
        assertEquals(Shift.FIRST.toString(), result.shift());

        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyMapper).toEntity(requestDTO, place);
        verify(vacancyRepository).save(vacancy);
        verify(vacancyMapper).toResponse(vacancy);
    }

    @Test
    @DisplayName("Should throw PlaceNotFoundException when place is not found during creation")
    void shouldThrowExceptionWhenPlaceNotFoundOnCreate() {
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.empty());

        assertThrows(PlaceNotFoundException.class, () -> vacancyService.create(requestDTO));

        verify(placeRepository).findById(PLACE_ID);
        verifyNoInteractions(vacancyMapper);
        verifyNoInteractions(vacancyRepository);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(NullPointerException.class, () -> vacancyService.create(null));
    }

    // --- FIND ALL & SEARCH TESTS ---

    @Test
    @DisplayName("Should return list of all vacancies")
    void shouldFindAllVacancies() {
        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        List<VacancyResponseDTO> result = vacancyService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));

        verify(vacancyRepository).findAll();
        verify(vacancyMapper).toResponse(vacancy);
    }

    @Test
    @DisplayName("Should search vacancies using filter specification and sort")
    @SuppressWarnings("unchecked")
    void shouldSearchVacanciesWithFilter() {
        VacancyFilter filter = mock(VacancyFilter.class);

        when(vacancyRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(vacancy));
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        List<VacancyResponseDTO> result = vacancyService.searchVacancies(filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(vacancyRepository).findAll(any(Specification.class), any(Sort.class));
        verify(vacancyMapper).toResponse(vacancy);
    }

    // --- FIND BY ID TESTS ---

    @Test
    @DisplayName("Should find vacancy by id and return response dto")
    void shouldFindVacancyById() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        VacancyResponseDTO result = vacancyService.findById(VACANCY_ID);

        assertNotNull(result);
        assertEquals(VACANCY_ID, result.id());
        assertEquals(Shift.FIRST.toString(), result.shift());

        verify(vacancyRepository).findById(VACANCY_ID);
        verify(vacancyMapper).toResponse(vacancy);
    }

    @Test
    @DisplayName("Should throw VacancyNotFoundException when finding non-existent vacancy by id")
    void shouldThrowExceptionWhenVacancyNotFoundById() {
        when(vacancyRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(VacancyNotFoundException.class, () -> vacancyService.findById(NON_EXISTENT_ID));

        verify(vacancyRepository).findById(NON_EXISTENT_ID);
        verifyNoInteractions(vacancyMapper);
    }

    // --- FIND BY NAME TESTS ---

    @Test
    @DisplayName("Should find vacancy by name and return response dto list")
    void shouldFindVacancyByName() {
        String name = "Fullstack";

        when(vacancyRepository.findByName(name)).thenReturn(List.of(vacancy));
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        List<VacancyResponseDTO> result = vacancyService.findByName(name);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(VACANCY_ID, result.get(0).id());

        verify(vacancyRepository).findByName(name);
        verify(vacancyMapper).toResponse(vacancy);
    }

    @Test
    @DisplayName("Should return empty list when finding non-existent vacancy by name")
    void shouldReturnEmptyListWhenVacancyNotFoundByName() {
        String name = "NonExistent";

        when(vacancyRepository.findByName(name)).thenReturn(List.of());

        List<VacancyResponseDTO> result = vacancyService.findByName(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(vacancyRepository).findByName(name);
        verifyNoInteractions(vacancyMapper);
    }

    // --- UPDATE TESTS ---

    @Test
    @DisplayName("Should update vacancy and return response dto")
    void shouldUpdateVacancy() {
        VacancyUpdateRequestDTO updateRequestDTO = new VacancyUpdateRequestDTO(
                "Fullstack Updated", "new description", Area.IT.toString(), Shift.SECOND.toString(), PLACE_ID
        );

        VacancyResponseDTO updatedResponse = new VacancyResponseDTO(
                VACANCY_ID, "Fullstack Updated", "new description", 10L, Area.IT.toString(), Shift.SECOND.toString(), Park.WEG_II.toString(), Section.IT.toString()
        );

        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toResponse(vacancy)).thenReturn(updatedResponse);

        VacancyResponseDTO result = vacancyService.update(VACANCY_ID, updateRequestDTO);

        assertNotNull(result);
        assertEquals(VACANCY_ID, result.id());
        assertEquals(Shift.SECOND.toString(), result.shift());

        verify(vacancyRepository).findById(VACANCY_ID);
        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyRepository).save(vacancy);
        verify(vacancyMapper).toResponse(vacancy);
    }

    @Test
    @DisplayName("Should throw VacancyNotFoundException when updating non-existent vacancy")
    void shouldThrowExceptionWhenUpdateVacancyNotFound() {
        VacancyUpdateRequestDTO updateRequestDTO = new VacancyUpdateRequestDTO(
                "Name", "Description", Area.IT.toString(), Shift.FIRST.toString(), PLACE_ID
        );

        when(vacancyRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(VacancyNotFoundException.class, () -> vacancyService.update(NON_EXISTENT_ID, updateRequestDTO));

        verify(vacancyRepository).findById(NON_EXISTENT_ID);
        verify(vacancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw PlaceNotFoundException when updating vacancy with non-existent place id")
    void shouldThrowExceptionWhenPlaceNotFoundOnUpdate() {
        VacancyUpdateRequestDTO updateRequestDTO = new VacancyUpdateRequestDTO(
                null, null, null, null, PLACE_ID
        );

        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.empty());

        assertThrows(PlaceNotFoundException.class, () -> vacancyService.update(VACANCY_ID, updateRequestDTO));

        verify(vacancyRepository).findById(VACANCY_ID);
        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update vacancy keeping existing values when request fields are null or blank")
    void shouldUpdateVacancyWithoutChangingBlankFields() {
        VacancyUpdateRequestDTO updateRequestDTO = new VacancyUpdateRequestDTO(
                "  ", "", "   ", "", null
        );

        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);

        VacancyResponseDTO result = vacancyService.update(VACANCY_ID, updateRequestDTO);

        assertNotNull(result);
        assertEquals("Fullstack", vacancy.getName());
        assertEquals("description", vacancy.getDescription());
        assertEquals(Area.IT, vacancy.getArea());
        assertEquals(Shift.FIRST, vacancy.getShift());

        verify(vacancyRepository).findById(VACANCY_ID);
        verify(placeRepository, never()).findById(any());
        verify(vacancyRepository).save(vacancy);
    }

    // --- DELETE TESTS ---

    @Test
    @DisplayName("Should delete vacancy")
    void shouldDeleteVacancy() {
        when(vacancyRepository.existsById(VACANCY_ID)).thenReturn(true);
        doNothing().when(vacancyRepository).deleteById(VACANCY_ID);

        assertDoesNotThrow(() -> vacancyService.delete(VACANCY_ID));

        verify(vacancyRepository).existsById(VACANCY_ID);
        verify(vacancyRepository).deleteById(VACANCY_ID);
    }

    @Test
    @DisplayName("Should throw VacancyNotFoundException when deleting non-existent vacancy")
    void shouldThrowExceptionWhenDeleteNotFound() {
        when(vacancyRepository.existsById(NON_EXISTENT_ID)).thenReturn(false);

        assertThrows(VacancyNotFoundException.class, () -> vacancyService.delete(NON_EXISTENT_ID));

        verify(vacancyRepository).existsById(NON_EXISTENT_ID);
        verify(vacancyRepository, never()).deleteById(any(UUID.class));
    }
}