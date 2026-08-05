package com.weg.quicktransfer.scheduler;

import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.service.ManagerService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InterviewReminderProcessor {

    private final InterviewRepository interviewRepository;
    private final ManagerService managerService;

    @Transactional
    public boolean process(UUID interviewId) throws MessagingException {
        Interview interview = interviewRepository.findByIdForUpdate(interviewId).orElse(null);
        if (interview == null || Boolean.TRUE.equals(interview.getReminderSent())) {
            return false;
        }

        managerService.sendInterviewEmail(interviewId);
        interview.setReminderSent(true);
        interviewRepository.save(interview);
        return true;
    }
}
