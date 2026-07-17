package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.interview.InterviewRequestDTO;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.mapper.InterviewMapper;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.service.InterviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewMapper interviewMapper;

    @InjectMocks
    private InterviewService interviewService;

    private Interview interview;
    private InterviewRequestDTO requestDTO;
    private InterviewResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        Student student = new Student();
        student.setId(1L);

        Manager manager = new Manager();
        manager.setId(2L);

        Place place = new Place();
        place.setId(3L);

        Vacancy vacancy = new Vacancy();
        vacancy.setId(4L);

        interview = new Interview();
        interview.setId(1L);
        interview.setDateTime(LocalDateTime.of(2026, 7, 16, 15, 30));
        interview.setStudent(student);
        interview.setManager(manager);
        interview.setPlace(place);
        interview.setVacancy(vacancy);

        // Inicialização utilizando os construtores canônicos dos Records
        requestDTO = new InterviewRequestDTO(LocalDateTime.of(2026, 7, 16, 15, 30), 1L, 2L, 3L, 4L);
        responseDTO = new InterviewResponseDTO(1L, LocalDateTime.of(2026, 7, 16, 15, 30), 1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("Should create interview and return response dto")
    void shouldCreateInterview() {
        when(interviewMapper.toEntity(requestDTO)).thenReturn(interview);
        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewMapper.toResponse(interview)).thenReturn(responseDTO);

        InterviewResponseDTO result = interviewService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id()); // Acesso ao componente id() do record
        assertEquals(1L, result.studentId());
        assertEquals(2L, result.managerId());
        assertEquals(3L, result.placeId());
        assertEquals(4L, result.vacancyId());

        verify(interviewMapper).toEntity(requestDTO);
        verify(interviewRepository).save(interview);
        verify(interviewMapper).toResponse(interview);
    }

    @Test
    @DisplayName("Should find interview by id and return response dto")
    void shouldFindInterviewById() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewMapper.toResponse(interview)).thenReturn(responseDTO);

        InterviewResponseDTO result = interviewService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.studentId());

        verify(interviewRepository).findById(1L);
        verify(interviewMapper).toResponse(interview);
    }

    @Test
    @DisplayName("Should update interview and return response dto")
    void shouldUpdateInterview() {
        // Records são imutáveis; criamos novas instâncias para representar dados modificados
        InterviewRequestDTO updatedRequest = new InterviewRequestDTO(LocalDateTime.of(2026, 7, 17, 10, 0), 1L, 2L, 3L, 4L);

        Interview updatedInterview = new Interview();
        updatedInterview.setId(1L);
        updatedInterview.setDateTime(LocalDateTime.of(2026, 7, 17, 10, 0));
        updatedInterview.setStudent(interview.getStudent());
        updatedInterview.setManager(interview.getManager());
        updatedInterview.setPlace(interview.getPlace());
        updatedInterview.setVacancy(interview.getVacancy());

        InterviewResponseDTO updatedResponse = new InterviewResponseDTO(1L, LocalDateTime.of(2026, 7, 17, 10, 0), 1L, 2L, 3L, 4L);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewMapper.toEntity(updatedRequest)).thenReturn(updatedInterview);
        when(interviewRepository.save(any(Interview.class))).thenReturn(updatedInterview);
        when(interviewMapper.toResponse(updatedInterview)).thenReturn(updatedResponse);

        InterviewResponseDTO result = interviewService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(LocalDateTime.of(2026, 7, 17, 10, 0), result.dateTime());

        verify(interviewRepository).findById(1L);
        verify(interviewMapper).toEntity(updatedRequest);
        verify(interviewRepository).save(any(Interview.class));
        verify(interviewMapper).toResponse(updatedInterview);
    }

    @Test
    @DisplayName("Should delete interview")
    void shouldDeleteInterview() {
        doNothing().when(interviewRepository).deleteById(1L);

        assertDoesNotThrow(() -> interviewService.delete(1L));

        verify(interviewRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> interviewService.create(null));
    }
}