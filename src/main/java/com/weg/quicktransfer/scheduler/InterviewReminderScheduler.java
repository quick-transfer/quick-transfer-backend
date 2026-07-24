package com.weg.quicktransfer.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.service.ManagerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewReminderScheduler {

    private final InterviewRepository interviewRepository;
    private final StudentRepository studentRepository;
    private final ManagerService managerService;

    /**
     * Executa a cada 5 minutos.
     * Procura entrevistas que acontecerão entre 24h e 24h05min a partir de agora.
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void sendInterviewReminders() {

        LocalDateTime start = LocalDateTime.now().plusHours(24);
        LocalDateTime end = start.plusMinutes(5);

        log.info(
                "Checking interview reminders between {} and {}",
                start,
                end
        );

        List<Interview> interviews = interviewRepository
                .findByDateTimeBetweenAndReminderSentFalse(start, end);

        if (interviews.isEmpty()) {
            return;
        }

        for (Interview interview : interviews) {
            try {

                Student student = studentRepository
                        .findByInterviewId(interview.getId())
                        .orElseThrow(() ->
                                new StudentNotFoundException(
                                        "Student not found for interview ID: "
                                                + interview.getId()));

                managerService.sendDynamicEmailAmp(
                        student.getEmail(),
                        interview.getId()
                );

                interview.setReminderSent(true);
                interviewRepository.save(interview);

                log.info(
                        "Reminder sent successfully to {} for interview {}",
                        student.getEmail(),
                        interview.getId()
                );

            } catch (Exception e) {

                log.error(
                        "Failed to send reminder for interview {}",
                        interview.getId(),
                        e
                );
            }
        }
    }
}