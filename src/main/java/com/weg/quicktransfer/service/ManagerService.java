package com.weg.quicktransfer.service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.weg.quicktransfer.dto.manager.*;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.exception.*;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.repo.specifications.ManagerSpecification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

@Service
@RequiredArgsConstructor
public class ManagerService {

  private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{14,}$";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;
    private final InterviewRepository interviewRepository;
    private final StudentRepository studentRepository;
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@quick-transfer.local}")
    private String mailFrom = "no-reply@quick-transfer.local";

    @Value("${app.frontend-url:https://quick-transfer.local}")
    private String frontendUrl = "https://quick-transfer.local";

    @Transactional
    public ManagerResponseDTO create(ManagerRequestDTO managerRequestDTO) {
        if (managerRequestDTO == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }

        Manager manager = managerMapper.toEntity(managerRequestDTO);
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        manager = managerRepository.save(manager);

        return managerMapper.toResponse(manager);
    }

    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> findAll() {
        return managerRepository.findAll().stream()
                .map(managerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ManagerResponseDTO> findAll(Pageable pageable) {
        return managerRepository.findAll(pageable).map(managerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ManagerResponseDTO findById(UUID id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return managerMapper.toResponse(manager);
    }

    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> searchManagers(ManagerFilter filter) {
        Specification<Manager> spec = ManagerSpecification.getFilteredManagers(filter);
        List<Manager> managers = managerRepository.findAll(spec);
        return managers.stream().map(managerMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ManagerResponseDTO> searchManagers(ManagerFilter filter, Pageable pageable) {
        Specification<Manager> spec = ManagerSpecification.getFilteredManagers(filter);
        return managerRepository.findAll(spec, pageable).map(managerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> findByName(String name) {
        List<Manager> managers = managerRepository.searchUsersByName(name);
        return managers.stream()
                .map(managerMapper::toResponse)
                .toList();
    }

    @Transactional
    public ManagerResponseDTO update(UUID id, ManagerUpdateRequestDTO updateRequestDTO, String requesterUsername) {

      User requester = userRepository.findFirstByUsername(requesterUsername)
                .orElseThrow(() -> new UserNotFoundException("User is not logged"));

      if (!(requester instanceof Manager || requester instanceof Admin)) {
            throw new UserNotAllowdException("User is neither an Admin nor a Manager");
        }
        if (!(requester instanceof Admin) && !id.equals(requester.getId())) {
            throw new UserNotAllowdException("User is not allowed to update this manager");
        }

        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            manager.setName(updateRequestDTO.name());
        }

        if (StringUtils.hasText(updateRequestDTO.password())) {
            if (!updateRequestDTO.password().matches(PASSWORD_REGEX)) {
                throw new InvalidPasswordException("Password does not meet security requirements.");
        }
        manager.setPassword(passwordEncoder.encode(updateRequestDTO.password()));
            manager.setTokenVersion(manager.getTokenVersion() + 1);
      }

        if (StringUtils.hasText(updateRequestDTO.section())) {
            manager.setSection(Section.valueOf(updateRequestDTO.section().trim().toUpperCase(Locale.ROOT)));
        }

        Manager managerUpdated = managerRepository.save(manager);
        return managerMapper.toResponse(managerUpdated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!managerRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        managerRepository.deleteById(id);
    }

    private Student findStudent(UUID interviewId) {
        return studentRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with the interview ID: " + interviewId));
    }

    @Transactional(readOnly = true)
    public void sendInterviewEmail(UUID interviewId) throws MessagingException {
        Student student = findStudent(interviewId);
        sendDynamicEmailAmp(student.getEmail(), interviewId);
    }

    public void sendInterviewEmail(UUID interviewId, String requesterUsername) throws MessagingException {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found with ID: " + interviewId));
        if (interview.getManager() == null
                || !requesterUsername.equals(interview.getManager().getUsername())) {
            throw new UserNotAllowdException("Manager is not responsible for this interview");
        }
        sendInterviewEmail(interviewId);
    }

        @Transactional(readOnly = true)
    public void sendDynamicEmailAmp(String to, UUID interviewId) throws MessagingException {
        validateEmail(to);
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found with ID: " + interviewId));
        Student student = findStudent(interviewId);
        Manager manager = managerRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with the interview ID: " + interviewId));

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
        helper.setFrom(mailFrom);
        helper.setTo(to);
        Coordinator coordinator = coordinatorOf(student);
        if (coordinator != null && StringUtils.hasText(coordinator.getEmail())) {
            validateEmail(coordinator.getEmail());
            helper.setCc(coordinator.getEmail());
        }
        helper.setSubject("Entrevista de emprego");
        helper.setText(buildHtml(interview, student, manager), true);
        mailSender.send(message);
    }

     private String buildHtml(Interview interview, Student student, Manager manager) {
        String body = """
                <!doctype html><html lang="pt-BR"><body style="font-family:Arial,sans-serif;color:#1f2937">
                <h2>Olá %s, você foi convidado(a) para uma entrevista</h2>
                <p><strong>Data:</strong> %s às %s</p>
                <p><strong>Local:</strong> %s — %s — %s</p>
                <p><strong>Entrevistador(a):</strong> %s</p>
                <p><strong>Gerente:</strong> %s</p>
                <p><strong>Vaga:</strong> %s</p>
                <p><a href="%s">Acessar o Quick Transfer</a></p>
                </body></html>
                """;
        return body.formatted(
                escape(student.getName()),
                DATE_FORMATTER.format(interview.getDateTime()),
                TIME_FORMATTER.format(interview.getDateTime()),
                escape(interview.getPlace().getPlaceName()),
                escape(interview.getPlace().getSection()),
                escape(interview.getPlace().getPark()),
                escape(interview.getInterviewerName()),
                escape(manager.getName()),
                escape(interview.getVacancy().getDescription()),
                escape(frontendUrl));
    }

    private Coordinator coordinatorOf(Student student) {
        if (student.getClassEntity() == null || student.getClassEntity().getCourse() == null) {
            return null;
        }
        return student.getClassEntity().getCourse().getCoordinator();
    }

    private String escape(Object value) {
        return HtmlUtils.htmlEscape(String.valueOf(value), StandardCharsets.UTF_8.name());
    }

    private void validateEmail(String email) {
         try {
            InternetAddress address = new InternetAddress(email, true);
            address.validate();
        } catch (AddressException ex) {
            throw new InvalidEmailException("Invalid e-mail address.");
        }
    }
}
