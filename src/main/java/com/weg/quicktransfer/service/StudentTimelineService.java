package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.student.StudentTimelineResponseDTO;
import com.weg.quicktransfer.enums.InterviewOutcome;
import com.weg.quicktransfer.enums.InterviewStatus;
import com.weg.quicktransfer.enums.TransferRequestStatus;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.ShiftTransferRequest;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.ShiftTransferRequestRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentTimelineService {
    private final StudentRepository studentRepository;
    private final ShiftTransferRequestRepository transferRepository;
    private final InterviewRepository interviewRepository;

    @Transactional(readOnly = true)
    public List<StudentTimelineResponseDTO> findByStudent(UUID studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        List<StudentTimelineResponseDTO> timeline = new ArrayList<>();

        transferRepository.findAllByStudentIdOrderByRequestedAtDesc(studentId).stream()
                .map(this::fromTransfer)
                .forEach(timeline::add);
        interviewRepository.findAllByStudentIdOrderByDateTimeDesc(studentId).stream()
                .map(this::fromInterview)
                .forEach(timeline::add);

        if (student.getAttendanceRate() != null && student.getAttendanceRate() < 75) {
            timeline.add(new StudentTimelineResponseDTO(
                    "attendance-" + studentId,
                    studentId,
                    "Alerta de frequência",
                    "A frequência atual de " + student.getAttendanceRate() + "% está abaixo do mínimo recomendado.",
                    LocalDateTime.now(),
                    "WARNING",
                    "warning"));
        }

        timeline.sort(Comparator.comparing(StudentTimelineResponseDTO::date).reversed());
        return timeline;
    }

    private StudentTimelineResponseDTO fromTransfer(ShiftTransferRequest request) {
        String title = switch (request.getStatus()) {
            case PENDING -> "Solicitação de troca de turno criada";
            case APPROVED -> "Solicitação de troca aprovada";
            case REJECTED -> "Solicitação de troca rejeitada";
        };
        String status = request.getStatus() == TransferRequestStatus.APPROVED
                ? "success"
                : request.getStatus() == TransferRequestStatus.REJECTED ? "danger" : "warning";
        return new StudentTimelineResponseDTO(
                "transfer-" + request.getId(),
                request.getStudent().getId(),
                title,
                "Mudança de " + request.getCurrentShift().getName() + " para "
                        + request.getTargetShift().getName() + ". Motivo: " + request.getReason(),
                request.getResolvedAt() == null ? request.getRequestedAt() : request.getResolvedAt(),
                "TRANSFER",
                status);
    }

    private StudentTimelineResponseDTO fromInterview(Interview interview) {
        String title = interview.getStatus() == InterviewStatus.CANCELLED
                ? "Entrevista cancelada"
                : interview.getOutcome() == InterviewOutcome.APPROVED
                        ? "Aprovado em entrevista"
                        : interview.getOutcome() == InterviewOutcome.REJECTED
                                ? "Resultado de entrevista registrado"
                                : "Entrevista agendada";
        String status = interview.getStatus() == InterviewStatus.CANCELLED
                ? "neutral"
                : interview.getOutcome() == InterviewOutcome.APPROVED
                        ? "success"
                        : interview.getOutcome() == InterviewOutcome.REJECTED ? "danger" : "info";
        return new StudentTimelineResponseDTO(
                "interview-" + interview.getId(),
                interview.getStudent().getId(),
                title,
                "Vaga " + interview.getVacancy().getName() + " com " + interview.getInterviewerName() + ".",
                interview.getDateTime(),
                "INTERVIEW",
                status);
    }
}
