package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.mapper.PlaceMapper;
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

import java.util.ArrayList;

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

        place = new Place();
        place.setId(1L);
        place.setName("Fábrica Jaraguá");
        place.setVacancies(new ArrayList<Vacancy>());

        requestDTO = new PlaceRequestDTO();
        requestDTO.setName("Fábrica Jaraguá");

        responseDTO = new PlaceResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Fábrica Jaraguá");
    }

    @Test
    @DisplayName("Should create place and return response dto")
    void shouldCreatePlace() {

        when(placeMapper.toEntity(requestDTO))
                .thenReturn(place);

        when(placeRepo.save(place))
                .thenReturn(place);

        when(placeMapper.toResponseDTO(place))
                .thenReturn(responseDTO);

        PlaceResponseDTO result =
                placeService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Fábrica Jaraguá", result.getName());

        verify(placeMapper).toEntity(requestDTO);
        verify(placeRepo).save(place);
        verify(placeMapper).toResponseDTO(place);
    }

    @Test
    @DisplayName("Should find place by id and return response dto")
    void shouldFindPlaceById() {

        when(placeRepo.findById(1L))
                .thenReturn(place);

        when(placeMapper.toResponseDTO(place))
                .thenReturn(responseDTO);

        PlaceResponseDTO result =
                placeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Fábrica Jaraguá", result.getName());

        verify(placeRepo).findById(1L);
        verify(placeMapper).toResponseDTO(place);
    }

    @Test
    @DisplayName("Should update place and return response dto")
    void shouldUpdatePlace() {

        PlaceRequestDTO updatedRequest =
                new PlaceRequestDTO();

        updatedRequest.setName("Fábrica Blumenau");

        Place updatedEntity = new Place();
        updatedEntity.setId(1L);
        updatedEntity.setName("Fábrica Blumenau");
        updatedEntity.setVacancies(new ArrayList<>());

        PlaceResponseDTO updatedResponse =
                new PlaceResponseDTO();

        updatedResponse.setId(1L);
        updatedResponse.setName("Fábrica Blumenau");

        when(placeRepo.findById(1L))
                .thenReturn(place);

        when(placeMapper.toEntity(updatedRequest))
                .thenReturn(updatedEntity);

        when(placeRepo.save(any(Place.class)))
                .thenReturn(updatedEntity);

        when(placeMapper.toResponseDTO(updatedEntity))
                .thenReturn(updatedResponse);

        PlaceResponseDTO result =
                placeService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Fábrica Blumenau", result.getName());

        verify(placeRepo).findById(1L);
        verify(placeMapper).toEntity(updatedRequest);
        verify(placeRepo).save(any(Place.class));
        verify(placeMapper).toResponseDTO(updatedEntity);
    }

    @Test
    @DisplayName("Should delete place")
    void shouldDeletePlace() {

        doNothing()
                .when(placeRepo)
                .deleteById(1L);

        assertDoesNotThrow(() ->
                placeService.delete(1L));

        verify(placeRepo)
                .deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.create(null)
        );
    }
}