package com.weg.quicktransfer.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.weg.quicktransfer.exception.InterviewNotFoundException;
import com.weg.quicktransfer.exception.InvalidEmailException;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.repo.ManagerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    private final InterviewRepository interviewRepository;

    private final StudentRepository studentRepository;

    private final JavaMailSender mailSender;

    @Transactional
    public ManagerResponseDTO create(ManagerRequestDTO managerRequestDTO) {
        if (managerRequestDTO == null) {
            throw new IllegalArgumentException("Manager can not be null");
        }

        Manager manager = managerMapper.toEntity(managerRequestDTO);

        managerRepository.save(manager);

        return managerMapper.toResponse(manager);
    }

    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> findAll() {
        List<Manager> managers = managerRepository.findAll();

        return managers.stream().map(managerMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ManagerResponseDTO findById(Long id) {
        Manager manager = managerRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return managerMapper.toResponse(manager);
    }

    @Transactional(readOnly = true)
    public ManagerResponseDTO findByName(String name) {
        Manager manager = managerRepository.findByName(name).orElseThrow(() -> new UserNotFoundException("User nof found with name: " + name));

        return managerMapper.toResponse(manager);
    }

    @Transactional
    public ManagerResponseDTO update(Long id, String name, String email, String password) {
        Manager manager = managerRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if(StringUtils.hasText(name)) {
            manager.setName(name);
        }

        if(StringUtils.hasText(email)) {
            manager.setEmail(email);
        }

        if(StringUtils.hasText(password) && password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{14,}$")) {
            manager.setPassword(password);
        }

        Manager managerAtt = managerRepository.save(manager);

        return managerMapper.toResponse(managerAtt);
    }

    @Transactional
    public void delete(Long id) {
        if(!managerRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        managerRepository.deleteById(id);
    }

    public void enviarEmailDinamicoAmp(String to, Long interviewId) throws MessagingException {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(to)) {
            throw new InvalidEmailException("Invalid e-mail: " + to);
        }

        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() -> new InterviewNotFoundException("Interview not found with ID: " + interviewId));

        Student student = studentRepository.findByInterviewId(interviewId).orElseThrow(() -> new StudentNotFoundException("Student not found with the interview ID" + interviewId));


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatedDate = formatter.format(interview.getDateTime());

        DateTimeFormatter formatter24 = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = formatter24.format(interview.getDateTime());

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom("quick.transfer.gmail@gmail.com");
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Entrevista de Emprego");

        MimeMultipart multipart = new MimeMultipart("alternative");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent("<p>Seu leitor não suporta e-mails interativos.</p>", "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        String corpoAmp = """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <title>Convite para Entrevista de Emprego</title>
              <style>
                body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }
                table { border-collapse: collapse !important; }
                body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; background-color: #f9fafb; font-family: Arial, Helvetica, sans-serif; color: #1f2937; }
            
                @media screen and (max-width: 620px) {
                  .email-container { width: 100% !important; max-width: 100% !important; }
                  .mobile-padding { padding-left: 20px !important; padding-right: 20px !important; }
                  .label-col { width: 100% !important; display: block !important; padding-bottom: 2px !important; padding-left: 12px !important; }
                  .value-col { width: 100% !important; display: block !important; padding-bottom: 10px !important; padding-left: 12px !important; }
                }
              </style>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f9fafb;">
            
              <table border="0" cellpadding="0" cellspacing="0" width="100%" role="presentation" style="background-color: #f9fafb; padding: 30px 12px;">
                <tr>
                  <td align="center">
            
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" class="email-container" style="max-width: 600px; background-color: #ffffff; border: 1px solid #e5e7eb; border-radius: 6px;">
            
                      <tr>
                        <td style="padding: 32px 36px 24px 36px; border-bottom: 1px solid #f3f4f6;" class="mobile-padding">
                          <p style="margin: 0 0 8px 0; font-size: 12px; font-weight: 600; color: #6b7280; text-transform: uppercase; letter-spacing: 0.5px;">
                            Recursos Humanos
                          </p>
                          <h1 style="color: #111827; font-size: 20px; font-weight: 700; line-height: 1.4; margin: 0;">
                            Olá""" + student.getName() + """
                          , você foi convidado(a) para uma entrevista de emprego em """ + interview.getPlace().getPlaceName() + """
                          </h1>
                        </td>
                      </tr>
            
                      <tr>
                        <td style="padding: 28px 36px 16px 36px;" class="mobile-padding">
                          <p style="margin: 0; font-size: 15px; line-height: 1.6; color: #374151;">
                            Após a análise do seu currículo, gostaríamos de agendar uma entrevista.
                          </p>
                        </td>
                      </tr>
            
                      <tr>
                        <td style="padding: 12px 36px 20px 36px;" class="mobile-padding">
                          <table border="0" cellpadding="0" cellspacing="0" width="100%" role="presentation" style="background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 4px;">
                            <tr>
                              <td style="padding: 20px 24px;">
            
                                <p style="margin: 0 0 14px 0; font-size: 12px; font-weight: 700; color: #4b5563; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid #e5e7eb; padding-bottom: 6px;">
                                  Informações da Entrevista
                                </p>
            
                                <table border="0" cellpadding="0" cellspacing="0" width="100%" role="presentation">
            
                                  <tr>
                                    <td class="label-col" width="36%" style="font-size: 14px; color: #6b7280; font-weight: 400; padding: 6px 12px 6px 12px; vertical-align: top;">
                                      Data:
                                    </td>
                                    <td class="value-col" width="64%" style="font-size: 14px; color: #111827; font-weight: 600; padding: 6px 12px 6px 0; vertical-align: top;">
                      """ + formatedDate + """
                                    </td>
                                  </tr>
            
                                  <tr>
                                    <td class="label-col" width="36%" style="font-size: 14px; color: #6b7280; font-weight: 400; padding: 6px 12px 6px 12px; vertical-align: top;">
                                      Horário:
                                    </td>
                                    <td class="value-col" width="64%" style="font-size: 14px; color: #111827; font-weight: 600; padding: 6px 12px 6px 0; vertical-align: top;">
                                  """ + formattedTime + """
                                    </td>
                                  </tr>
            
                                  <tr>
                                    <td class="label-col" width="36%" style="font-size: 14px; color: #6b7280; font-weight: 400; padding: 6px 12px 6px 12px; vertical-align: top;">
                                      Local:
                                    </td>
                                    <td class="value-col" width="64%" style="font-size: 14px; color: #111827; font-weight: 500; padding: 6px 12px 6px 0; vertical-align: top;">
                                  """ + interview.getPlace().getPlaceName() +"""
                                    </td>
                                  </tr>
            
                                  <tr>
                                    <td class="label-col" width="36%" style="font-size: 14px; color: #6b7280; font-weight: 400; padding: 6px 12px 6px 12px; vertical-align: top;">
                                      Setor:
                                    </td>
                                    <td class="value-col" width="64%" style="font-size: 14px; color: #111827; font-weight: 500; padding: 6px 12px 6px 0; vertical-align: top;">
                                  """ + interview.getPlace().getSection().toString() + """
                                    </td>
                                  </tr>
            
                                  <tr>
                                    <td class="label-col" width="36%" style="font-size: 14px; color: #6b7280; font-weight: 400; padding: 6px 12px 6px 12px; vertical-align: top;">
                                      Parque Fabril:
                                    </td>
                                    <td class="value-col" width="64%" style="font-size: 14px; color: #111827; font-weight: 500; padding: 6px 12px 6px 0; vertical-align: top;">
                                  """ + interview.getPlace().getPark().toString() + """
                                    </td>
                                  </tr>
            
                                  <tr>
                                    <td class="label-col" width="36%" style="font-size: 14px; color: #6b7280; font-weight: 400; padding: 6px 12px 6px 12px; vertical-align: top;">
                                      Entrevistador(a):
                                    </td>
                                    <td class="value-col" width="64%" style="font-size: 14px; color: #111827; font-weight: 500; padding: 6px 12px 6px 0; vertical-align: top;">
                                  """ + interview.getInterviewerName() + """
                                    </td>
                                  </tr>
            
                                  <tr>
                                    <td class="label-col" width="36%" style="font-size: 14px; color: #6b7280; font-weight: 400; padding: 6px 12px 6px 12px; vertical-align: top;">
                                      Gerente do Setor:
                                    </td>
                                    <td class="value-col" width="64%" style="font-size: 14px; color: #111827; font-weight: 500; padding: 6px 12px 6px 0; vertical-align: top;">
                                  """ + managerRepository.findByInterviewId(interviewId).orElseThrow(() -> new UserNotFoundException("Manager not found with the interview ID: " + interviewId)) + """
                                    </td>
                                  </tr>
            
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
            
                      <tr>
                        <td style="padding: 8px 36px 20px 36px;" class="mobile-padding">
                          <p style="margin: 0 0 8px 0; font-size: 14px; font-weight: 700; color: #111827;">
                            Descrição da Vaga
                          </p>
                          <p style="margin: 0; font-size: 14px; line-height: 1.6; color: #374151; background-color: #ffffff; border: 1px solid #e5e7eb; border-radius: 4px; padding: 14px 18px;">
                      """ + interview.getVacancy().getDescription() + """
                          </p>
                        </td>
                      </tr>
            
                      <tr>
                        <td style="padding: 0 36px 24px 36px;" class="mobile-padding">
                          <div style="background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 4px; padding: 14px 18px;">
                            <p style="margin: 0 0 4px 0; font-size: 13px; font-weight: 700; color: #374151;">
                              Aviso para acesso à fábrica:
                            </p>
                            <p style="margin: 0; font-size: 13px; color: #6b7280; line-height: 1.5;">
                              É necessário apresentar crachá na portaria e utilizar calçado fechado para circulação no parque fabril.
                            </p>
                          </div>
                        </td>
                      </tr>
            
                      <!-- Botão redirecionando para localhost:3000 -->
                      <tr>
                        <td style="padding: 0 36px 32px 36px; text-align: left;" class="mobile-padding">
                          <p style="margin: 0 0 14px 0; font-size: 14px; color: #374151;">
                            Favor responder a este e-mail confirmando o seu recebimento e presença.
                          </p>
                          <a href="http://localhost:3000" style="background-color: #374151; color: #ffffff; text-decoration: none; padding: 11px 22px; font-size: 14px; font-weight: 600; border-radius: 4px; display: inline-block;">
                            Confirmar Presença
                          </a>
                        </td>
                      </tr>
            
                      <tr>
                        <td style="background-color: #f9fafb; padding: 20px 36px; border-top: 1px solid #e5e7eb; font-size: 13px; color: #6b7280; line-height: 1.4;" class="mobile-padding">
                          <p style="margin: 0 0 2px 0; font-weight: 600; color: #4b5563;">
                            Departamento de Recursos Humanos
                          </p>
                          <p style="margin: 0;">
                            [local]
                          </p>
                        </td>
                      </tr>
            
                    </table>
            
                  </td>
                </tr>
              </table>
            
            </body>
            </html>
            """;

        MimeBodyPart ampPart = new MimeBodyPart();
        ampPart.setContent(corpoAmp, "text/x-amp-html; charset=utf-8");
        multipart.addBodyPart(ampPart);

        message.setContent(multipart);
        mailSender.send(message);
    }
}