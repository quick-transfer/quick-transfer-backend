//package com.weg.quicktransfer;
//
//import com.weg.quicktransfer.dto.interview.InterviewRequestDTO;
//import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
//import com.weg.quicktransfer.dto.interview.InterviewUpdateRequestDTO;
//import com.weg.quicktransfer.enums.Park;
//import com.weg.quicktransfer.enums.Section;
//import com.weg.quicktransfer.enums.Shift;
//import com.weg.quicktransfer.exception.InterviewNotFoundException;
//import com.weg.quicktransfer.exception.PlaceNotFoundException;
//import com.weg.quicktransfer.mapper.InterviewMapper;
//import com.weg.quicktransfer.model.Interview;
//import com.weg.quicktransfer.model.Manager;
//import com.weg.quicktransfer.model.Place;
//import com.weg.quicktransfer.model.Student;
//import com.weg.quicktransfer.model.Vacancy;
//import com.weg.quicktransfer.repo.InterviewRepository;
//import com.weg.quicktransfer.repo.ManagerRepository;
//import com.weg.quicktransfer.repo.PlaceRepository;
//import com.weg.quicktransfer.repo.StudentRepository;
//import com.weg.quicktransfer.repo.VacancyRepository;
//import com.weg.quicktransfer.service.InterviewService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class InterviewServiceTest {
//
//    @Mock
//    private InterviewRepository interviewRepository;
//
//    @Mock
//    private InterviewMapper interviewMapper;
//
//    @Mock
//    private PlaceRepository placeRepository;
//
//    @Mock
//    private VacancyRepository vacancyRepository;
//
//    @Mock
//    private ManagerRepository managerRepository;
//
//    @Mock
//    private StudentRepository studentRepository;
//
//    @InjectMocks
//    private InterviewService interviewService;
//
//    private Interview interview;
//    private InterviewRequestDTO requestDTO;
//    private InterviewResponseDTO responseDTO;
//    private Student student;
//    private Manager manager;
//    private Place place;
//    private Vacancy vacancy;
//
//    @BeforeEach
//    void setUp() {
//        student = new Student();
//        student.setId(1L);
//        student.setName("Bruno");
//
//        manager = new Manager();
//        manager.setId(2L);
//        manager.setName("Guilherme");
//
//        place = new Place();
//        place.setId(3L);
//        place.setPark(Park.WEG_II);
//        place.setSection(Section.TI);
//
//        vacancy = new Vacancy();
//        vacancy.setId(4L);
//        vacancy.setName("vacancy");
//        vacancy.setShift(Shift.FIRST);
//
//        interview = new Interview();
//        interview.setId(1L);
//        interview.setInterviewerName("interview");
//        interview.setDateTime(LocalDateTime.of(2026, 7, 16, 15, 30));
//        interview.setStudent(student);
//        interview.setManager(manager);
//        interview.setPlace(place);
//        interview.setVacancy(vacancy);
//
//        requestDTO = new InterviewRequestDTO("interview", LocalDateTime.of(2026, 7, 16, 15, 30), 3L, 4L, 2L, 1L);
//        responseDTO = new InterviewResponseDTO(1L, "interview", LocalDateTime.of(2026, 7, 16, 15, 30), Park.WEG_II.toString(), Section.TI.toString(), "Bruno", "Guilherme", Shift.FIRST.toString());
//    }
//
//    @Test
//    @DisplayName("Should create interview and return response dto")
//    void shouldCreateInterview() {
//        when(placeRepository.findById(3L)).thenReturn(Optional.of(place));
//        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
//        when(managerRepository.findById(2L)).thenReturn(Optional.of(manager));
//        when(studentRepository.findById(4L)).thenReturn(Optional.of(student));
//
//        when(interviewMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(interview);
//        when(interviewRepository.save(any())).thenReturn(interview);
//        when(interviewMapper.toResponse(any())).thenReturn(responseDTO);
//
//        InterviewResponseDTO result = interviewService.create(requestDTO);
//
//        assertNotNull(result);
//        verify(vacancyRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Should throw PlaceNotFoundException when place does not exist on create")
//    void shouldThrowPlaceNotFoundExceptionOnCreate() {
//        when(placeRepository.findById(3L)).thenReturn(Optional.empty());
//
//        assertThrows(PlaceNotFoundException.class, () -> interviewService.create(requestDTO));
//
//        verify(placeRepository).findById(3L);
//        verifyNoInteractions(vacancyRepository, managerRepository, studentRepository, interviewMapper, interviewRepository);
//    }
//
//    @Test
//    @DisplayName("Should find interview by id and return response dto")
//    void shouldFindInterviewById() {
//        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
//        when(interviewMapper.toResponse(interview)).thenReturn(responseDTO);
//
//        InterviewResponseDTO result = interviewService.findById(1L);
//
//        assertNotNull(result);
//        assertEquals(1L, result.id());
//        assertEquals("Bruno", result.nameStudent());
//
//        verify(interviewRepository).findById(1L);
//        verify(interviewMapper).toResponse(interview);
//    }
//
//    @Test
//    @DisplayName("Should throw InterviewNotFoundException when find by id does not exist")
//    void shouldThrowInterviewNotFoundExceptionWhenFindByIdNotFound() {
//        when(interviewRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(InterviewNotFoundException.class, () -> interviewService.findById(1L));
//
//        verify(interviewRepository).findById(1L);
//        verifyNoInteractions(interviewMapper);
//    }
//
//    @Test
//    @DisplayName("Should find all interviews and return list of response dto")
//    void shouldFindAllInterviews() {
//        when(interviewRepository.findAll()).thenReturn(List.of(interview));
//        when(interviewMapper.toResponse(interview)).thenReturn(responseDTO);
//
//        List<InterviewResponseDTO> result = interviewService.findAll();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(1L, result.get(0).id());
//
//        verify(interviewRepository).findAll();
//        verify(interviewMapper).toResponse(interview);
//    }
//
//    @Test
//    @DisplayName("Should update interview and return response dto")
//    void shouldUpdateInterview() {
//        LocalDateTime newDateTime = LocalDateTime.of(2026, 7, 17, 10, 0);
//        InterviewUpdateRequestDTO updateRequest = new InterviewUpdateRequestDTO("New Interviewer", newDateTime, 3L, 1L, 2L, 1L);
//
//        InterviewResponseDTO updatedResponse = new InterviewResponseDTO(1L, "New Interviewer", newDateTime, Park.WEG_II.toString(), Section.TI.toString(), "Bruno", "Guilherme", Shift.FIRST.toString());
//
//        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
//        when(placeRepository.findById(3L)).thenReturn(Optional.of(place));
//        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
//        when(managerRepository.findById(2L)).thenReturn(Optional.of(manager));
//        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
//
//        when(interviewRepository.save(any())).thenReturn(interview);
//        when(interviewMapper.toResponse(any())).thenReturn(updatedResponse);
//
//        InterviewResponseDTO result = interviewService.update(1L, updateRequest);
//
//        assertNotNull(result);
//        verify(vacancyRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Should throw InterviewNotFoundException when updating non-existent interview")
//    void shouldThrowExceptionWhenUpdateNotFound() {
//        InterviewUpdateRequestDTO updateRequest = new InterviewUpdateRequestDTO("Interviewer", null, null, null, null, null);
//
//        when(interviewRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(InterviewNotFoundException.class, () -> interviewService.update(1L, updateRequest));
//
//        verify(interviewRepository).findById(1L);
//        verify(interviewRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Should delete interview successfully")
//    void shouldDeleteInterview() {
//        when(interviewRepository.existsById(1L)).thenReturn(true);
//        doNothing().when(interviewRepository).deleteById(1L);
//
//        assertDoesNotThrow(() -> interviewService.delete(1L));
//
//        verify(interviewRepository).existsById(1L);
//        verify(interviewRepository).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("Should throw InterviewNotFoundException when deleting non-existent interview")
//    void shouldThrowExceptionWhenDeleteNotFound() {
//        when(interviewRepository.existsById(1L)).thenReturn(false);
//
//        assertThrows(InterviewNotFoundException.class, () -> interviewService.delete(1L));
//
//        verify(interviewRepository).existsById(1L);
//        verify(interviewRepository, never()).deleteById(anyLong());
//    }
//}