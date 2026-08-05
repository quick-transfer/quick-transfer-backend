package com.weg.quicktransfer;

import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.scheduler.InterviewReminderProcessor;
import com.weg.quicktransfer.scheduler.InterviewReminderScheduler;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewReminderSchedulerTest {

    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private InterviewReminderProcessor reminderProcessor;

    private InterviewReminderScheduler scheduler;
    private UUID interviewId;

    @BeforeEach
    void setUp() {
        scheduler = new InterviewReminderScheduler(interviewRepository, reminderProcessor);
        interviewId = UUID.randomUUID();
        when(interviewRepository.findPendingReminderIds(any(), any(), any()))
                .thenReturn(List.of(interviewId));
    }

    @Test
    void shouldProcessPendingReminder() throws Exception {
        when(reminderProcessor.process(interviewId)).thenReturn(true);

        scheduler.sendInterviewReminders();

        verify(reminderProcessor).process(interviewId);
    }

    @Test
    void shouldIgnoreReminderAlreadyProcessed() throws Exception {
        when(reminderProcessor.process(interviewId)).thenReturn(false);

        scheduler.sendInterviewReminders();

        verify(reminderProcessor).process(interviewId);
    }

    @Test
    void shouldContinueWhenProcessingFails() throws Exception {
        doThrow(new MessagingException("SMTP unavailable"))
                .when(reminderProcessor).process(interviewId);

        scheduler.sendInterviewReminders();

        verify(reminderProcessor).process(interviewId);
    }
}
