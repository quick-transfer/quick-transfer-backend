package com.weg.quicktransfer;

import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.scheduler.InterviewReminderScheduler;
import com.weg.quicktransfer.service.ManagerService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewReminderSchedulerTest {

    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private ManagerService managerService;

    private InterviewReminderScheduler scheduler;
    private Interview interview;

    @BeforeEach
    void setUp() {
        scheduler = new InterviewReminderScheduler(interviewRepository, managerService);
        interview = new Interview();
        interview.setId(UUID.randomUUID());
        interview.setDateTime(LocalDateTime.now().plusHours(12));
        when(interviewRepository.findByDateTimeBetweenAndReminderSentFalse(any(), any()))
                .thenReturn(List.of(interview));
    }

    @Test
    void shouldSendOnlyAfterClaimingReminder() throws Exception {
        when(interviewRepository.claimReminder(any(), any(), any())).thenReturn(1);

        scheduler.sendInterviewReminders();

        verify(managerService).sendDynamicEmailAmp(interview.getId());
        verify(interviewRepository, never()).releaseReminderClaim(any());
    }

    @Test
    void shouldSkipReminderClaimedByAnotherInstance() throws Exception {
        when(interviewRepository.claimReminder(any(), any(), any())).thenReturn(0);

        scheduler.sendInterviewReminders();

        verifyNoInteractions(managerService);
    }

    @Test
    void shouldReleaseClaimWhenSendingFails() throws Exception {
        when(interviewRepository.claimReminder(any(), any(), any())).thenReturn(1);
        doThrow(new MessagingException("SMTP unavailable"))
                .when(managerService).sendDynamicEmailAmp(interview.getId());

        scheduler.sendInterviewReminders();

        verify(interviewRepository).releaseReminderClaim(interview.getId());
    }
}
