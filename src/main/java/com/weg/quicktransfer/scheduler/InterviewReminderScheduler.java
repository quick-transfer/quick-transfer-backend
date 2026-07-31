package com.weg.quicktransfer.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.service.ManagerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewReminderScheduler {

    private final InterviewRepository interviewRepository;
    private final ManagerService managerService;

    /**
     * Executa a cada 5 minutos.
     * Processa entrevistas ainda não notificadas que acontecerão nas próximas 24 horas.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void sendInterviewReminders() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(24);

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
            int claimed = interviewRepository.claimReminder(
                    interview.getId(),
                    start,
                    start.minusMinutes(15));
            if (claimed == 0) {
                continue;
            }

            try {
                managerService.sendDynamicEmailAmp(interview.getId());

                log.info(
                        "Reminder sent successfully for interview {}",
                        interview.getId()
                );

            } catch (Exception e) {

                log.error(
                        "Failed to send reminder for interview {}",
                        interview.getId(),
                        e
                );
                interviewRepository.releaseReminderClaim(interview.getId());
            }
        }
    }
}
