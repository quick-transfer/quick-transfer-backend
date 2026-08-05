package com.weg.quicktransfer.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.repo.InterviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewReminderScheduler {

    private final InterviewRepository interviewRepository;
    
    private final InterviewReminderProcessor reminderProcessor;

    @Value("${app.reminders.look-ahead-hours:24}")
    private long lookAheadHours;

    @Value("${app.reminders.batch-size:100}")
    private int batchSize;

    @Value("${app.reminders.zone:America/Sao_Paulo}")
    private String reminderZone;

    @Scheduled(
            cron = "${app.reminders.cron:0 */5 * * * *}",
            zone = "${app.reminders.zone:America/Sao_Paulo}")
    public void sendInterviewReminders() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(reminderZone));
        LocalDateTime deadline = now.plusHours(lookAheadHours);
        List<UUID> interviewIds = interviewRepository.findPendingReminderIds(
                now,
                deadline,
                PageRequest.of(0, Math.max(1, Math.min(batchSize, 1000))));

        for (UUID interviewId : interviewIds) {    

            try {
                if (reminderProcessor.process(interviewId)) {
                    log.info("Reminder sent successfully for interview {}", interviewId);
                }
            } catch (Exception ex) {
                log.error("Failed to send reminder for interview {}", interviewId, ex);
            }
        }
    }
}
