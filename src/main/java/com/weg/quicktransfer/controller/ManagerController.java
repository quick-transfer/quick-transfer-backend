package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.dto.manager.ManagerUpdateRequestDTO;
import com.weg.quicktransfer.service.ManagerService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/create")
    public ResponseEntity<ManagerResponseDTO> createManager(@RequestBody ManagerRequestDTO managerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(managerService.create(managerRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<ManagerResponseDTO> findManagerById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<ManagerResponseDTO>> findCManagerByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findByName(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<ManagerResponseDTO>> findAllManagerss() {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findAll());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ManagerResponseDTO> updateManager(
            @PathVariable Long id,
            @RequestBody ManagerUpdateRequestDTO updateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.update(id, updateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        managerService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/interview/sendEmail/{interviewId}")
    public ResponseEntity<String> postSendInterviewEmail(
            @PathVariable Long interviewId,
            @RequestParam("email") String to,
            @RequestHeader(value = "AMP-Same-Origin", required = false) String sameOrigin,
            @RequestHeader(value = "AMP-Email-Sender", required = false) String sender)  throws MessagingException {

        managerService.sendEmail(to, interviewId);

        return ResponseEntity.ok()
                .header("AMP-Email-Allow-Sender", "quick.transfer.gmail@gmail.com")
                .body("{\"message\": \"success!\"}");
    }
}
