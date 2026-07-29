package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;

public record InterviewFilter(
        String interviewerName,

        LocalDateTime dateTime,

        String vacancyName,

        String placeName,

        String managerName,

        String studentName,

        Boolean reminderSent
) {
}
