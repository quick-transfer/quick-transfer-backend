package com.weg.quicktransfer.dto.student;

import com.weg.quicktransfer.enums.StatusStudent;
import com.weg.quicktransfer.enums.StudentInterviewStatus;

import java.time.LocalDateTime;
import java.util.List;

public record StudentFilter(
        String name,

        String email,

        Long age,

        Double averageGrade,

        String courseName,

        StudentInterviewStatus studentInterviewStatus,

        Boolean hasSeenEmail,

        StatusStudent statusStudent,

        LocalDateTime interviewDateTime,

        List<String> skillsName
) {
}
