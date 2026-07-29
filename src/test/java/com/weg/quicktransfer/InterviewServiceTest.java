package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.dto.interview.InterviewRequestDTO;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.dto.interview.InterviewUpdateRequestDTO;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.exception.InterviewNotFoundException;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.exception.VacancyNotFoundException;
import com.weg.quicktransfer.mapper.InterviewMapper;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.service.InterviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewMapper interviewMapper;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private InterviewService interviewService;

    private Interview interview;
    private InterviewRequestDTO requestDTO;
    private InterviewResponseDTO responseDTO;
    private Student student;
    private Manager manager;
    private Place place;
    private Vacancy vacancy;

    private static final UUID INTERVIEW_ID    = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID STUDENT_ID      = UUID.fromString("123e4567-e89b-12d3-a456-426614174111");
    private static final UUID MANAGER_ID      = UUID.fromString("123e4567-e89b-12d3-a456-426614174222");
    private static final UUID PLACE_ID        = UUID.fromString("123e4567-e89b-12d3-a456-426614174333");
    private static final UUID VACANCY_ID      = UUID.fromString("123e4567-e89b-12d3-a456-426614174444");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174999");

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(STUDENT_ID);
        student.setName("Bruno");

        manager = new Manager();
        manager.setId(MANAGER_ID);
        manager.setName("Guilherme");

        place = new Place();
        place.setId(PLACE_ID);
        place.setPark(Park.WEG_II);
        place.setSection(Section.IT);

        vacancy = new Vacancy();
        vacancy.setId(VACANCY_ID);
        vacancy.setName("vacancy");
        vacancy.setShift(Shift.FIRST);

        interview = new Interview();
        interview.setId(INTERVIEW_ID);
        interview.setInterviewerName("interview");
        interview.setDateTime(LocalDateTime.of(2026, 7, 16, 15, 30));
        interview.setStudent(student);
        interview.setManager(manager);
        interview.setPlace(place);
        interview.setVacancy(vacancy);

        requestDTO = new InterviewRequestDTO("interview", LocalDateTime.of(2026, 7, 16, 15, 30), PLACE_ID, VACANCY_ID, MANAGER_ID, STUDENT_ID);
        responseDTO = new InterviewResponseDTO(INTERVIEW_ID, "interview", LocalDateTime.of(2026, 7, 16, 15, 30), Park.WEG_II.toString(), Section.IT.toString(), "Bruno", "Guilherme", Shift.FIRST.toString());
    }

    @Test
    @DisplayName("Should create interview and return response dto")
    void shouldCreateInterview() {
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(managerRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));

        when(interviewMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(interview);
        when(interviewRepository.save(any())).thenReturn(interview);
        when(interviewMapper.toResponse(any())).thenReturn(responseDTO);

        InterviewResponseDTO result = interviewService.create(requestDTO);

        assertNotNull(result);
        assertEquals(INTERVIEW_ID, result.id());
        assertEquals("Bruno", result.nameStudent());

        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyRepository).findById(VACANCY_ID);
        verify(managerRepository).findById(MANAGER_ID);
        verify(studentRepository).findById(STUDENT_ID);
        verify(interviewRepository).save(any());
        verify(interviewMapper).toResponse(any());
    }

    @Test
    @DisplayName("Should throw PlaceNotFoundException when place does not exist on create")
    void shouldThrowPlaceNotFoundExceptionOnCreate() {
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.empty());

        assertThrows(PlaceNotFoundException.class, () -> interviewService.create(requestDTO));

        verify(placeRepository).findById(PLACE_ID);
        verifyNoInteractions(vacancyRepository, managerRepository, studentRepository, interviewMapper, interviewRepository);
    }

    @Test
    @DisplayName("Should throw VacancyNotFoundException when vacancy does not exist on create")
    void shouldThrowVacancyNotFoundExceptionOnCreate() {
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        assertThrows(VacancyNotFoundException.class, () -> interviewService.create(requestDTO));

        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyRepository).findById(VACANCY_ID);
        verifyNoInteractions(managerRepository, studentRepository, interviewMapper, interviewRepository);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when manager does not exist on create")
    void shouldThrowUserNotFoundExceptionOnCreate() {
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(managerRepository.findById(MANAGER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> interviewService.create(requestDTO));

        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyRepository).findById(VACANCY_ID);
        verify(managerRepository).findById(MANAGER_ID);
        verifyNoInteractions(studentRepository, interviewMapper, interviewRepository);
    }

    @Test
    @DisplayName("Should throw StudentNotFoundException when student does not exist on create")
    void shouldThrowStudentNotFoundExceptionOnCreate() {
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(managerRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> interviewService.create(requestDTO));

        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyRepository).findById(VACANCY_ID);
        verify(managerRepository).findById(MANAGER_ID);
        verify(studentRepository).findById(STUDENT_ID);
        verifyNoInteractions(interviewMapper, interviewRepository);
    }

    @Test
    @DisplayName("Should find interview by id and return response dto")
    void shouldFindInterviewById() {
        when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(interview));
        when(interviewMapper.toResponse(interview)).thenReturn(responseDTO);

        InterviewResponseDTO result = interviewService.findById(INTERVIEW_ID);

        assertNotNull(result);
        assertEquals(INTERVIEW_ID, result.id());
        assertEquals("Bruno", result.nameStudent());

        verify(interviewRepository).findById(INTERVIEW_ID);
        verify(interviewMapper).toResponse(interview);
    }

    @Test
    @DisplayName("Should throw InterviewNotFoundException when find by id does not exist")
    void shouldThrowInterviewNotFoundExceptionWhenFindByIdNotFound() {
        when(interviewRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(InterviewNotFoundException.class, () -> interviewService.findById(NON_EXISTENT_ID));

        verify(interviewRepository).findById(NON_EXISTENT_ID);
        verifyNoInteractions(interviewMapper);
    }

    @Test
    @DisplayName("Should find all interviews and return list of response dto")
    void shouldFindAllInterviews() {
        when(interviewRepository.findAll()).thenReturn(List.of(interview));
        when(interviewMapper.toResponse(interview)).thenReturn(responseDTO);

        List<InterviewResponseDTO> result = interviewService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(INTERVIEW_ID, result.get(0).id());

        verify(interviewRepository).findAll();
        verify(interviewMapper).toResponse(interview);
    }

    @Test
    @DisplayName("Should search interviews using filter specification")
    @SuppressWarnings("unchecked")
    void shouldSearchInterviewsWithFilter() {
        InterviewFilter filter = new InterviewFilter("interview", LocalDateTime.of(2026, 7, 16, 15, 30), vacancy.getName(), place.getPlaceName(), manager.getName(), student.getName(), true);

        when(interviewRepository.findAll(any(Specification.class))).thenReturn(List.of(interview));
        when(interviewMapper.toResponse(interview)).thenReturn(responseDTO);

        List<InterviewResponseDTO> result = interviewService.searchInterviews(filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(interviewRepository).findAll(any(Specification.class));
        verify(interviewMapper).toResponse(interview);
    }

    @Test
    @DisplayName("Should update interview and return response dto")
    void shouldUpdateInterview() {
        LocalDateTime newDateTime = LocalDateTime.of(2026, 7, 17, 10, 0);
        InterviewUpdateRequestDTO updateRequest = new InterviewUpdateRequestDTO("New Interviewer", newDateTime, PLACE_ID, VACANCY_ID, MANAGER_ID, STUDENT_ID);

        InterviewResponseDTO updatedResponse = new InterviewResponseDTO(INTERVIEW_ID, "New Interviewer", newDateTime, Park.WEG_II.toString(), Section.IT.toString(), "Bruno", "Guilherme", Shift.FIRST.toString());

        when(interviewRepository.findById(INTERVIEW_ID)).thenReturn(Optional.of(interview));
        when(placeRepository.findById(PLACE_ID)).thenReturn(Optional.of(place));
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
        when(managerRepository.findById(MANAGER_ID)).thenReturn(Optional.of(manager));
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));

        when(interviewRepository.save(any())).thenReturn(interview);
        when(interviewMapper.toResponse(any())).thenReturn(updatedResponse);

        InterviewResponseDTO result = interviewService.update(INTERVIEW_ID, updateRequest);

        assertNotNull(result);
        assertEquals("New Interviewer", result.interviewerName());

        verify(interviewRepository).findById(INTERVIEW_ID);
        verify(placeRepository).findById(PLACE_ID);
        verify(vacancyRepository).findById(VACANCY_ID);
        verify(managerRepository).findById(MANAGER_ID);
        verify(studentRepository).findById(STUDENT_ID);
        verify(interviewRepository).save(any());
        verify(interviewMapper).toResponse(any());
    }

    @Test
    @DisplayName("Should throw InterviewNotFoundException when updating non-existent interview")
    void shouldThrowExceptionWhenUpdateNotFound() {
        InterviewUpdateRequestDTO updateRequest = new InterviewUpdateRequestDTO("Interviewer", null, null, null, null, null);

        when(interviewRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(InterviewNotFoundException.class, () -> interviewService.update(NON_EXISTENT_ID, updateRequest));

        verify(interviewRepository).findById(NON_EXISTENT_ID);
        verify(interviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete interview successfully")
    void shouldDeleteInterview() {
        when(interviewRepository.existsById(INTERVIEW_ID)).thenReturn(true);
        doNothing().when(interviewRepository).deleteById(INTERVIEW_ID);

        assertDoesNotThrow(() -> interviewService.delete(INTERVIEW_ID));

        verify(interviewRepository).existsById(INTERVIEW_ID);
        verify(interviewRepository).deleteById(INTERVIEW_ID);
    }

    @Test
    @DisplayName("Should throw InterviewNotFoundException when deleting non-existent interview")
    void shouldThrowExceptionWhenDeleteNotFound() {
        when(interviewRepository.existsById(NON_EXISTENT_ID)).thenReturn(false);

        assertThrows(InterviewNotFoundException.class, () -> interviewService.delete(NON_EXISTENT_ID));

        verify(interviewRepository).existsById(NON_EXISTENT_ID);
        verify(interviewRepository, never()).deleteById(any(UUID.class));
    }
}