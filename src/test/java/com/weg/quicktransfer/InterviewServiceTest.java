package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.InterviewRequestDTO;
import com.weg.quicktransfer.dto.InterviewResponseDTO;
import com.weg.quicktransfer.mapper.InterviewMapper;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.InterviewRepo;
import com.weg.quicktransfer.service.InterviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepo interviewRepo;

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

        requestDTO = new InterviewRequestDTO();
        requestDTO.setDateTime(LocalDateTime.of(2026, 7, 16, 15, 30));
        requestDTO.setStudentId(1L);
        requestDTO.setManagerId(2L);
        requestDTO.setPlaceId(3L);
        requestDTO.setVacancyId(4L);

        responseDTO = new InterviewResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setDateTime(LocalDateTime.of(2026, 7, 16, 15, 30));
        responseDTO.setStudentId(1L);
        responseDTO.setManagerId(2L);
        responseDTO.setPlaceId(3L);
        responseDTO.setVacancyId(4L);
    }

    @Test
    @DisplayName("Should create interview and return response dto")
    void shouldCreateInterview() {
        when(interviewMapper.toEntity(requestDTO)).thenReturn(interview);
        when(interviewRepo.save(interview)).thenReturn(interview);
        when(interviewMapper.toResponseDTO(interview)).thenReturn(responseDTO);

        InterviewResponseDTO result = interviewService.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getStudentId());
        assertEquals(2L, result.getManagerId());
        assertEquals(3L, result.getPlaceId());
        assertEquals(4L, result.getVacancyId());

        verify(interviewMapper).toEntity(requestDTO);
        verify(interviewRepo).save(interview);
        verify(interviewMapper).toResponseDTO(interview);
    }

    @Test
    @DisplayName("Should find interview by id and return response dto")
    void shouldFindInterviewById() {
        when(interviewRepo.findById(1L)).thenReturn(interview);
        when(interviewMapper.toResponseDTO(interview)).thenReturn(responseDTO);

        InterviewResponseDTO result = interviewService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getStudentId());

        verify(interviewRepo).findById(1L);
        verify(interviewMapper).toResponseDTO(interview);
    }

    @Test
    @DisplayName("Should update interview and return response dto")
    void shouldUpdateInterview() {
        InterviewRequestDTO updatedRequest = new InterviewRequestDTO();
        updatedRequest.setDateTime(LocalDateTime.of(2026, 7, 17, 10, 0));
        updatedRequest.setStudentId(1L);
        updatedRequest.setManagerId(2L);
        updatedRequest.setPlaceId(3L);
        updatedRequest.setVacancyId(4L);

        Interview updatedInterview = new Interview();
        updatedInterview.setId(1L);
        updatedInterview.setDateTime(LocalDateTime.of(2026, 7, 17, 10, 0));
        updatedInterview.setStudent(interview.getStudent());
        updatedInterview.setManager(interview.getManager());
        updatedInterview.setPlace(interview.getPlace());
        updatedInterview.setVacancy(interview.getVacancy());

        InterviewResponseDTO updatedResponse = new InterviewResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setDateTime(LocalDateTime.of(2026, 7, 17, 10, 0));
        updatedResponse.setStudentId(1L);
        updatedResponse.setManagerId(2L);
        updatedResponse.setPlaceId(3L);
        updatedResponse.setVacancyId(4L);

        when(interviewRepo.findById(1L)).thenReturn(interview);
        when(interviewMapper.toEntity(updatedRequest)).thenReturn(updatedInterview);
        when(interviewRepo.save(any(Interview.class))).thenReturn(updatedInterview);
        when(interviewMapper.toResponseDTO(updatedInterview)).thenReturn(updatedResponse);

        InterviewResponseDTO result = interviewService.update(1L, updatedRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LocalDateTime.of(2026, 7, 17, 10, 0), result.getDateTime());

        verify(interviewRepo).findById(1L);
        verify(interviewMapper).toEntity(updatedRequest);
        verify(interviewRepo).save(any(Interview.class));
        verify(interviewMapper).toResponseDTO(updatedInterview);
    }

    @Test
    @DisplayName("Should delete interview")
    void shouldDeleteInterview() {
        doNothing().when(interviewRepo).deleteById(1L);

        assertDoesNotThrow(() -> interviewService.delete(1L));

        verify(interviewRepo).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when request dto is null")
    void shouldThrowExceptionWhenRequestDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> interviewService.create(null));
    }
}