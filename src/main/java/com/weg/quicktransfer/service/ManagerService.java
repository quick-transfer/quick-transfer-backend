package com.weg.quicktransfer.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.dto.manager.*;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.exception.*;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.specifications.ManagerSpecification;
import com.weg.quicktransfer.security.PasswordPolicy;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final PasswordEncoder passwordEncoder;
    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;
    private final InterviewRepository interviewRepository;
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public ManagerResponseDTO create(ManagerRequestDTO managerRequestDTO) {
        if (managerRequestDTO == null) {
            throw new IllegalArgumentException("Manager can not be null");
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
    public ManagerResponseDTO update(UUID id, ManagerUpdateRequestDTO updateRequestDTO) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            manager.setName(updateRequestDTO.name());
        }

        if (StringUtils.hasText(updateRequestDTO.password())) {
            PasswordPolicy.validate(updateRequestDTO.password());
            manager.setPassword(passwordEncoder.encode(updateRequestDTO.password()));
        }

        if (StringUtils.hasText(updateRequestDTO.section())) {
            manager.setSection(Section.valueOf(updateRequestDTO.section()));
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

    public void sendDynamicEmailAmp(UUID interviewId) throws MessagingException {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found with ID: " + interviewId));
        Student student = interview.getStudent();
        Manager manager = interview.getManager();

        if (student == null) {
            throw new StudentNotFoundException("Student not found with the interview ID: " + interviewId);
        }
        if (manager == null) {
            throw new UserNotFoundException("Manager not found with the interview ID: " + interviewId);
        }

        boolean sendStudent = !Boolean.TRUE.equals(interview.getStudentReminderSent());
        if (sendStudent) {
            validateEmail(student.getEmail());
        }

        Coordinator coordinator = findCoordinator(student);
        boolean coordinatorHasEmail = coordinator != null && StringUtils.hasText(coordinator.getEmail());
        boolean sendCoordinator = coordinatorHasEmail
                && !Boolean.TRUE.equals(interview.getCoordinatorReminderSent());
        if (sendCoordinator) {
            validateEmail(coordinator.getEmail());
        }

        MimeMessage studentMessage = sendStudent
                ? createEmailMessage(student.getEmail(), interview, student, manager)
                : null;
        MimeMessage coordinatorMessage = sendCoordinator
                ? createEmailMessageToCoordinator(coordinator.getEmail(), interview, student, manager, coordinator)
                : null;

        if (!coordinatorHasEmail) {
            interview.setCoordinatorReminderSent(true);
        }

        if (studentMessage != null) {
            mailSender.send(studentMessage);
            interview.setStudentReminderSent(true);
            interview = interviewRepository.save(interview);
        }
        if (coordinatorMessage != null) {
            mailSender.send(coordinatorMessage);
            interview.setCoordinatorReminderSent(true);
            interview = interviewRepository.save(interview);
        }

        interview.setReminderSent(
                Boolean.TRUE.equals(interview.getStudentReminderSent())
                        && Boolean.TRUE.equals(interview.getCoordinatorReminderSent()));
        interview.setReminderProcessing(false);
        interview.setReminderClaimedAt(null);
        interviewRepository.save(interview);
    }

    private MimeMessage createEmailMessage(
            String to,
            Interview interview,
            Student student,
            Manager manager) throws MessagingException {
        String formattedDate = DATE_FORMATTER.format(interview.getDateTime());
        String formattedTime = TIME_FORMATTER.format(interview.getDateTime());

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(mailFrom);
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Entrevista de Emprego");

        MimeMultipart multipart = new MimeMultipart("alternative");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent("<p>Seu leitor não suporta e-mails interativos.</p>", "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        String htmlContent = buildHtmlBody(interview, student, manager, formattedDate, formattedTime);

        MimeBodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(htmlContent, "text/html; charset=utf-8");
        multipart.addBodyPart(contentPart);

        message.setContent(multipart);
        return message;
    }

    private String buildHtmlBody(Interview interview, Student student, Manager manager, String date, String time) {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Convite para Entrevista de Emprego</title>
              <style>
                body { margin: 0; padding: 0; background-color: #f9fafb; font-family: Arial, sans-serif; color: #1f2937; }
                .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e5e7eb; border-radius: 6px; }
              </style>
            </head>
            <body>
              <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="padding: 30px 12px;">
                <tr>
                  <td align="center">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%%" class="email-container">
                      <tr>
                        <td style="padding: 32px; border-bottom: 1px solid #f3f4f6;">
                          <p style="margin: 0 0 8px 0; font-size: 12px; font-weight: 600; color: #6b7280; text-transform: uppercase;">Recursos Humanos</p>
                          <h1 style="color: #111827; font-size: 20px; font-weight: 700; margin: 0;">
                            Olá %s, você foi convidado(a) para uma entrevista de emprego em %s
                          </h1>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 28px 32px 16px 32px;">
                          <p style="margin: 0; font-size: 15px; color: #374151;">Após a análise do seu currículo, gostaríamos de agendar uma entrevista.</p>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 12px 32px 20px 32px;">
                          <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 4px; padding: 16px;">
                            <tr>
                              <td>
                                <p style="margin: 0 0 12px 0; font-size: 12px; font-weight: 700; color: #4b5563; text-transform: uppercase;">Informações da Entrevista</p>
                                <p><strong>Data:</strong> %s</p>
                                <p><strong>Horário:</strong> %s</p>
                                <p><strong>Local:</strong> %s</p>
                                <p><strong>Setor:</strong> %s</p>
                                <p><strong>Parque Fabril:</strong> %s</p>
                                <p><strong>Entrevistador(a):</strong> %s</p>
                                <p><strong>Gerente do Setor:</strong> %s</p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 8px 32px 20px 32px;">
                          <p style="margin: 0 0 8px 0; font-size: 14px; font-weight: 700;">Descrição da Vaga</p>
                          <p style="margin: 0; font-size: 14px; background-color: #ffffff; border: 1px solid #e5e7eb; padding: 14px;">%s</p>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 0 32px 24px 32px;">
                          <div style="background-color: #f9fafb; border: 1px solid #e5e7eb; padding: 14px;">
                            <p style="margin: 0 0 4px 0; font-size: 13px; font-weight: 700;">Aviso para acesso à fábrica:</p>
                            <p style="margin: 0; font-size: 13px; color: #6b7280;">É necessário apresentar crachá na portaria e utilizar calçado fechado.</p>
                          </div>
                        </td>
                      </tr>
            
                      <tr>
                        <td style="padding: 0 32px 32px 32px;">
                          <a href="%s" style="background-color: #374151; color: #ffffff; text-decoration: none; padding: 11px 22px; font-size: 14px; font-weight: 600; border-radius: 4px; display: inline-block;">Confirmar Presença</a>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(
                escape(student.getName()),
                escape(interview.getPlace().getPlaceName()),
                escape(date),
                escape(time),
                escape(interview.getPlace().getPlaceName()),
                escape(interview.getPlace().getSection()),
                escape(interview.getPlace().getPark()),
                escape(interview.getInterviewerName()),
                escape(manager.getName()),
                escape(interview.getVacancy().getDescription()),
                escape(frontendUrl)
        );
    }

    private MimeMessage createEmailMessageToCoordinator(
            String to,
            Interview interview,
            Student student,
            Manager manager,
            Coordinator coordinator) throws MessagingException {
        String formattedDate = DATE_FORMATTER.format(interview.getDateTime());
        String formattedTime = TIME_FORMATTER.format(interview.getDateTime());

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(mailFrom);
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Alerta de Entrevista do Aluno");

        MimeMultipart multipart = new MimeMultipart("alternative");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent("<p>Seu leitor não suporta e-mails interativos.</p>", "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        String htmlContent = buildHtmlBodyForCoordinator(interview, student, coordinator, manager, formattedDate, formattedTime);

        MimeBodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(htmlContent, "text/html; charset=utf-8");
        multipart.addBodyPart(contentPart);

        message.setContent(multipart);
        return message;
    }

    private String buildHtmlBodyForCoordinator(Interview interview, Student student, Coordinator coordinator, Manager manager, String date, String time) {
        return """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Alerta de Entrevista do Aluno</title>
          <style>
            body { margin: 0; padding: 0; background-color: #f9fafb; font-family: Arial, sans-serif; color: #1f2937; }
            .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e5e7eb; border-radius: 6px; }
          </style>
        </head>
        <body>
          <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="padding: 30px 12px;">
            <tr>
              <td align="center">
                <table border="0" cellpadding="0" cellspacing="0" width="100%%" class="email-container">
                  <tr>
                    <td style="padding: 32px; border-bottom: 1px solid #f3f4f6;">
                      <p style="margin: 0 0 8px 0; font-size: 12px; font-weight: 600; color: #6b7280; text-transform: uppercase;">Coordenação</p>
                      <h1 style="color: #111827; font-size: 20px; font-weight: 700; margin: 0;">
                        Olá %s, o aluno %s foi convidado para uma entrevista
                      </h1>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding: 28px 32px 16px 32px;">
                      <p style="margin: 0; font-size: 15px; color: #374151;">
                        Este é um aviso para que você acompanhe o processo de entrevista do aluno vinculado à sua coordenação.
                      </p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding: 12px 32px 20px 32px;">
                      <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 4px; padding: 16px;">
                        <tr>
                          <td>
                            <p style="margin: 0 0 12px 0; font-size: 12px; font-weight: 700; color: #4b5563; text-transform: uppercase;">Informações da Entrevista</p>
                            <p><strong>Aluno:</strong> %s</p>
                            <p><strong>Data:</strong> %s</p>
                            <p><strong>Horário:</strong> %s</p>
                            <p><strong>Local:</strong> %s</p>
                            <p><strong>Setor:</strong> %s</p>
                            <p><strong>Parque Fabril:</strong> %s</p>
                            <p><strong>Entrevistador(a):</strong> %s</p>
                            <p><strong>Gerente do Setor:</strong> %s</p>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding: 8px 32px 20px 32px;">
                      <p style="margin: 0 0 8px 0; font-size: 14px; font-weight: 700;">Descrição da Vaga</p>
                      <p style="margin: 0; font-size: 14px; background-color: #ffffff; border: 1px solid #e5e7eb; padding: 14px;">%s</p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding: 0 32px 24px 32px;">
                      <div style="background-color: #f9fafb; border: 1px solid #e5e7eb; padding: 14px;">
                        <p style="margin: 0 0 4px 0; font-size: 13px; font-weight: 700;">Acompanhamento da coordenação:</p>
                        <p style="margin: 0; font-size: 13px; color: #6b7280;">Caso necessário, oriente o aluno sobre documentação, postura e comparecimento no horário informado.</p>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding: 0 32px 32px 32px;">
                      <a href="%s" style="background-color: #374151; color: #ffffff; text-decoration: none; padding: 11px 22px; font-size: 14px; font-weight: 600; border-radius: 4px; display: inline-block;">Acessar Sistema</a>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(
                escape(coordinator.getName()),
                escape(student.getName()),
                escape(student.getName()),
                escape(date),
                escape(time),
                escape(interview.getPlace().getPlaceName()),
                escape(interview.getPlace().getSection()),
                escape(interview.getPlace().getPark()),
                escape(interview.getInterviewerName()),
                escape(manager.getName()),
                escape(interview.getVacancy().getDescription()),
                escape(frontendUrl)
        );
    }

    private Coordinator findCoordinator(Student student) {
        if (student.getClassEntity() == null || student.getClassEntity().getCourse() == null) {
            return null;
        }
        return student.getClassEntity().getCourse().getCoordinator();
    }

    private String escape(Object value) {
        return HtmlUtils.htmlEscape(String.valueOf(value));
    }

    private void validateEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new InvalidEmailException("Invalid e-mail: " + email);
        }
    }
}
