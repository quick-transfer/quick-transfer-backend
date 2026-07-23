package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.service.ManagerService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/interview/sendEmail/{interviewId}")
    public ResponseEntity<String> postSendInterviewEmail(
            @PathVariable Long interviewId,
            @RequestParam("email") String to,
            @RequestHeader(value = "AMP-Same-Origin", required = false) String sameOrigin,
            @RequestHeader(value = "AMP-Email-Sender", required = false) String sender)  throws MessagingException {

        managerService.enviarEmailDinamicoAmp(to, interviewId);

        return ResponseEntity.ok()
                .header("AMP-Email-Allow-Sender", "quick.transfer.gmail@gmail.com")
                .body("{\"message\": \"success!\"}");
    }
}
