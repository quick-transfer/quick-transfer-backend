package com.weg.quicktransfer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.service.InterviewService;
import com.weg.quicktransfer.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InterviewFlowRulesTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InterviewService interviewService;

    @Test
    public void scheduleInterviewShouldScheduleSendEmailAndAssociateCorrectly() {
        Manager manager = new Manager(); manager.setName("John Manager");
        Student student = new Student(); student.setEmail("student@weg.net");

        Interview interview = new Interview();
        interview.setManager(manager);
        interview.setStudent(student);
        interview.setDateTime(LocalDateTime.now().plusDays(2));

        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        interviewService.scheduleInterview(interview);

        verify(interviewRepository, times(1)).save(interview);
        verify(emailService, times(1)).sendInterviewNotification(
                eq("student@weg.net"),
                contains("John Manager")
        );
    }

    @Test
    public void confirmInterviewParticipationShouldChangeStatusToViewed() {
        Interview interview = new Interview();
        Student student = new Student();
        student.setStudentInterviewStatus(StudentInterviewStatus.NAO_VISTO);
        interview.setStudent(student);

        when(interviewRepository.findById(10L)).thenReturn(Optional.of(interview));

        interviewService.confirmInterviewVisualization(10L);

        assertEquals(StudentInterviewStatus.VISTO, student.getStudentInterviewStatus());
        verify(studentRepository, times(1)).save(student);
    }
}