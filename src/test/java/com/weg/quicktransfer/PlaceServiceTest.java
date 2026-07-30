package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.place.PlaceFilter;
import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.dto.place.PlaceUpdateRequestDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.mapper.PlaceMapper;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepo;

    @Mock
    private PlaceMapper placeMapper;

    @InjectMocks
    private PlaceService placeService;

    private Place place;
    private PlaceRequestDTO requestDTO;
    private PlaceResponseDTO responseDTO;

    private static final UUID PLACE_ID        = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @BeforeEach
    void setUp() {
        place = new Place();
        place.setId(PLACE_ID);
        place.setPlaceName("Fábrica Jaraguá");
        place.setPark(Park.WEG_II);
        place.setSection(Section.IT);
        place.setVacancies(new ArrayList<Vacancy>());
        place.setInterviews(new ArrayList<Interview>());

        requestDTO = new PlaceRequestDTO(place.getPlaceName(), place.getPark().toString(), place.getSection().toString());

        responseDTO = new PlaceResponseDTO(place.getId(), place.getPlaceName(), place.getPark().toString(), place.getSection().toString());
    }

    @Test
    @DisplayName("Should create place and return response dto")
    void shouldCreatePlace() {
        when(placeMapper.toEntity(requestDTO)).thenReturn(place);
        when(placeRepo.save(place)).thenReturn(place);
        when(placeMapper.toResponse(place)).thenReturn(responseDTO);

        PlaceResponseDTO result = placeService.create(requestDTO);

        assertNotNull(result);
        assertEquals(PLACE_ID, result.id());
        assertEquals("Fábrica Jaraguá", result.placeName());

        verify(placeMapper).toEntity(requestDTO);
        verify(placeRepo).save(place);
        verify(placeMapper).toResponse(place);
    }

    @Test
    @DisplayName("Should find place by id and return response dto")
    void shouldFindPlaceById() {
        when(placeRepo.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(placeMapper.toResponse(place)).thenReturn(responseDTO);

        PlaceResponseDTO result = placeService.findById(PLACE_ID);

        assertNotNull(result);
        assertEquals(PLACE_ID, result.id());
        assertEquals("Fábrica Jaraguá", result.placeName());

        verify(placeRepo).findById(PLACE_ID);
        verify(placeMapper).toResponse(place);
    }

    @Test
    @DisplayName("Should throw exception when place not found by id")
    void shouldThrowExceptionWhenPlaceNotFoundById() {
        when(placeRepo.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(PlaceNotFoundException.class, () -> placeService.findById(NON_EXISTENT_ID));
    }

    @Test
    @DisplayName("Should return all places")
    void shouldFindAllPlaces() {
        when(placeRepo.findAll()).thenReturn(List.of(place));
        when(placeMapper.toResponse(place)).thenReturn(responseDTO);

        List<PlaceResponseDTO> result = placeService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(placeRepo).findAll();
    }

    @Test
    @DisplayName("Should update place and return response dto")
    void shouldUpdatePlace() {
        PlaceUpdateRequestDTO updatedRequest = new PlaceUpdateRequestDTO("Fábrica Blumenau", null, null);

        Place updatedEntity = new Place();
        updatedEntity.setId(PLACE_ID);
        updatedEntity.setPlaceName("Fábrica Blumenau");
        updatedEntity.setPark(Park.WEG_II);
        updatedEntity.setSection(Section.IT);
        updatedEntity.setVacancies(new ArrayList<>());
        updatedEntity.setInterviews(new ArrayList<>());

        PlaceResponseDTO updatedResponse = new PlaceResponseDTO(PLACE_ID, "Fábrica Blumenau", Park.WEG_II.toString(), Section.IT.toString());

        when(placeRepo.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(placeRepo.save(any(Place.class))).thenReturn(updatedEntity);
        when(placeMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

        PlaceResponseDTO result = placeService.update(PLACE_ID, updatedRequest);

        assertNotNull(result);
        assertEquals(PLACE_ID, result.id());
        assertEquals("Fábrica Blumenau", result.placeName());

        verify(placeRepo).findById(PLACE_ID);
        verify(placeRepo).save(any(Place.class));
        verify(placeMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Should search places with filter")
    void shouldSearchPlacesWithFilter() {
        PlaceFilter filter = new PlaceFilter("Jaraguá", Park.WEG_II, Section.IT);

        when(placeRepo.findAll(any(Specification.class))).thenReturn(List.of(place));
        when(placeMapper.toResponse(place)).thenReturn(responseDTO);

        List<PlaceResponseDTO> result = placeService.searchPlaces(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(placeRepo).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should throw exception when search filter is null")
    void shouldThrowExceptionWhenSearchFilterIsNull() {
        assertThrows(NullFilterException.class, () -> placeService.searchPlaces(null));
    }

    @Test
    @DisplayName("Should delete place")
    void shouldDeletePlace() {
        when(placeRepo.existsById(PLACE_ID)).thenReturn(true);

        assertDoesNotThrow(() -> placeService.delete(PLACE_ID));

        verify(placeRepo).existsById(PLACE_ID);
        verify(placeRepo).deleteById(PLACE_ID);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing place")
    void shouldThrowExceptionWhenDeletingNonExistingPlace() {
        when(placeRepo.existsById(NON_EXISTENT_ID)).thenReturn(false);

        assertThrows(PlaceNotFoundException.class, () -> placeService.delete(NON_EXISTENT_ID));
    }
}