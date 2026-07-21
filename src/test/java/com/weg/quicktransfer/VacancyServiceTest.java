//package com.weg.quicktransfer;
//
//import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
//import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
//import com.weg.quicktransfer.mapper.VacancyMapper;
//import com.weg.quicktransfer.model.Interview;
//import com.weg.quicktransfer.model.Place;
//import com.weg.quicktransfer.enums.Shift;
//import com.weg.quicktransfer.model.Vacancy;
//import com.weg.quicktransfer.repo.VacancyRepository;
//import com.weg.quicktransfer.service.VacancyService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class VacancyServiceTest {
//
//    @Mock
//    private VacancyRepository vacancyRepository;
//
//    @Mock
//    private VacancyMapper vacancyMapper;
//
//    @InjectMocks
//    private VacancyService vacancyService;
//
//    private Vacancy vacancy;
//    private VacancyRequestDTO requestDTO;
//    private VacancyResponseDTO responseDTO;
//    private Place place;
//
//    @BeforeEach
//    void setUp() {
//        place = new Place();
//        place.setId(1L);
//
//        vacancy = new Vacancy();
//        vacancy.setId(1L);
//        vacancy.setShift(Shift.FIRST);
//        vacancy.setPlace(place);
//        vacancy.setInterviews(new ArrayList<Interview>());
//
//        // Inicialização utilizando os construtores canônicos dos Records
//        requestDTO = new VacancyRequestDTO(Shift.FIRST, 1L);
//        responseDTO = new VacancyResponseDTO(1L, Shift.FIRST, 1L);
//    }
//
//    @Test
//    @DisplayName("Should create vacancy and return response dto")
//    void shouldCreateVacancy() {
//        when(vacancyMapper.toEntity(requestDTO)).thenReturn(vacancy);
//        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
//        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);
//
//        VacancyResponseDTO result = vacancyService.create(requestDTO);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id()); // Acesso ao componente id() do record
//        assertEquals(Shift.FIRST, result.shift());
//        assertEquals(1L, result.placeId());
//
//        verify(vacancyMapper).toEntity(requestDTO);
//        verify(vacancyRepository).save(vacancy);
//        verify(vacancyMapper).toResponse(vacancy);
//    }
//
//    @Test
//    @DisplayName("Should find vacancy by id and return response dto")
//    void shouldFindVacancyById() {
//        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
//        when(vacancyMapper.toResponse(vacancy)).thenReturn(responseDTO);
//
//        VacancyResponseDTO result = vacancyService.findById(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id());
//        assertEquals(Shift.FIRST, result.shift());
//
//        verify(vacancyRepository).findById(1L);
//        verify(vacancyMapper).toResponse(vacancy);
//    }
//
//    @Test
//    @DisplayName("Should update vacancy and return response dto")
//    void shouldUpdateVacancy() {
//        // Records são imutáveis; novas instâncias representam as modificações de dados
//        VacancyRequestDTO updatedRequest = new VacancyRequestDTO(Shift.SECOND, 1L);
//
//        Vacancy updatedEntity = new Vacancy();
//        updatedEntity.setId(1L);
//        updatedEntity.setShift(Shift.SECOND);
//        updatedEntity.setPlace(place);
//        updatedEntity.setInterviews(new ArrayList<Interview>());
//
//        VacancyResponseDTO updatedResponse = new VacancyResponseDTO(1L, Shift.SECOND, 1L);
//
//        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
//        when(vacancyMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
//        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(updatedEntity);
//        when(vacancyMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);
//
//        VacancyResponseDTO result = vacancyService.update(1L, updatedRequest);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id());
//        assertEquals(Shift.SECOND, result.shift());
//
//        verify(vacancyRepository).findById(1L);
//        verify(vacancyMapper).toEntity(updatedRequest);
//        verify(vacancyRepository).save(any(Vacancy.class));
//        verify(vacancyMapper).toResponse(updatedEntity);
//    }
//
//    @Test
//    @DisplayName("Should delete vacancy")
//    void shouldDeleteVacancy() {
//        doNothing().when(vacancyRepository).deleteById(1L);
//
//        assertDoesNotThrow(() -> vacancyService.delete(1L));
//
//        verify(vacancyRepository).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("Should throw exception when request dto is null")
//    void shouldThrowExceptionWhenRequestDtoIsNull() {
//        assertThrows(IllegalArgumentException.class, () -> vacancyService.create(null));
//    }
//}