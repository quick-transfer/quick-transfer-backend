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
import com.weg.quicktransfer.model.Place;
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

import java.util.List;

import java.util.ArrayList;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        place = new Place("Fábrica Jaraguá", Park.WEG_II, Section.TI);
        place.setId(1L);

        requestDTO = new PlaceRequestDTO("Fábrica Jaraguá", "WEG_II", "TI");
        responseDTO = new PlaceResponseDTO(1L, "Fábrica Jaraguá", "WEG_II", "TI");

        place = new Place();
        place.setId(1L);
        place.setPlaceName("Fábrica Jaraguá");
        place.setPark(Park.WEG_II);
        place.setSection(Section.TI);
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

        when(placeMapper.toEntity(requestDTO))
                .thenReturn(place);

        when(placeRepo.save(place))
                .thenReturn(place);

        when(placeMapper.toResponse(place))
                .thenReturn(responseDTO);

        PlaceResponseDTO result =
                placeService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Fábrica Jaraguá", result.placeName());

        verify(placeMapper).toEntity(requestDTO);
        verify(placeRepo).save(place);
        verify(placeMapper).toResponse(place);
    }

    @Test
    @DisplayName("Should find place by id and return response dto")
    void shouldFindPlaceById() {
        when(placeRepo.findById(1L)).thenReturn(Optional.of(place));
        when(placeMapper.toResponse(place)).thenReturn(responseDTO);

        PlaceResponseDTO result = placeService.findById(1L);

        when(placeRepo.findById(1L))
                .thenReturn(Optional.of(place));

        when(placeMapper.toResponse(place))
                .thenReturn(responseDTO);

        PlaceResponseDTO result =
                placeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Fábrica Jaraguá", result.placeName());

        verify(placeRepo).findById(1L);
        verify(placeMapper).toResponse(place);
    }

    @Test
    @DisplayName("Should throw exception when place not found by id")
    void shouldThrowExceptionWhenPlaceNotFoundById() {
        when(placeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PlaceNotFoundException.class, () -> placeService.findById(99L));
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
        PlaceUpdateRequestDTO updateDTO = new PlaceUpdateRequestDTO("Fábrica Blumenau", "WEG_II", "TI");
        Place updatedPlace = new Place("Fábrica Blumenau", Park.WEG_II, Section.TI);
        updatedPlace.setId(1L);
        PlaceResponseDTO updatedResponse = new PlaceResponseDTO(1L, "Fábrica Blumenau", "WEG_II", "TI");

        when(placeRepo.findById(1L)).thenReturn(Optional.of(place));
        when(placeRepo.save(any(Place.class))).thenReturn(updatedPlace);
        when(placeMapper.toResponse(updatedPlace)).thenReturn(updatedResponse);

        PlaceResponseDTO result = placeService.update(1L, updateDTO);
    @DisplayName("Should update place and return response dto")
    void shouldUpdatePlace() {

        PlaceUpdateRequestDTO updatedRequest = new PlaceUpdateRequestDTO("Fábrica Blumenau", null, null);

        Place updatedEntity = new Place();
        updatedEntity.setId(1L);
        updatedEntity.setPlaceName("Fábrica Blumenau");
        updatedEntity.setPark(Park.WEG_II);
        updatedEntity.setSection(Section.TI);
        updatedEntity.setVacancies(new ArrayList<>());
        updatedEntity.setInterviews(new ArrayList<>());

        PlaceResponseDTO updatedResponse = new PlaceResponseDTO(1L, "Fábrica Blumenau", Park.WEG_II.toString(), Section.TI.toString());


        when(placeRepo.findById(1L))
                .thenReturn(Optional.of(place));

        when(placeRepo.save(any(Place.class)))
                .thenReturn(updatedEntity);

        when(placeMapper.toResponse(updatedEntity))
                .thenReturn(updatedResponse);

        PlaceResponseDTO result =
                placeService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Fábrica Blumenau", result.placeName());

        verify(placeRepo).findById(1L);
        verify(placeRepo).save(any(Place.class));
    }

    @Test
    @DisplayName("Should search places with filter")
    void shouldSearchPlacesWithFilter() {
        PlaceFilter filter = new PlaceFilter("Jaraguá", Park.WEG_II, Section.TI);

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
    @DisplayName("Should delete place when exists")
    void shouldDeletePlace() {
        when(placeRepo.existsById(1L)).thenReturn(true);
        doNothing().when(placeRepo).deleteById(1L);
        verify(placeMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Should delete place")
    void shouldDeletePlace() {
        when(placeRepo.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> placeService.delete(1L));

        verify(placeRepo).existsById(1L);
        verify(placeRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing place")
    void shouldThrowExceptionWhenDeletingNonExistingPlace() {
        when(placeRepo.existsById(99L)).thenReturn(false);

        assertThrows(PlaceNotFoundException.class, () -> placeService.delete(99L));
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.create(null)
        );
    }
}