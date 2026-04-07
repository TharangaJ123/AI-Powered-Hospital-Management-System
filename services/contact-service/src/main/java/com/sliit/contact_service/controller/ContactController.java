package com.sliit.contact_service.controller;

import com.sliit.contact_service.dto.ContactRequest;
import com.sliit.contact_service.model.ContactMessage;
import com.sliit.contact_service.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/submit")
    public ResponseEntity<ContactMessage> submitMessage(@RequestBody ContactRequest request) {
        ContactMessage savedMessage = contactService.saveMessage(request);
        return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    @GetMapping("/user/{patientId}")
    public ResponseEntity<List<ContactMessage>> getPatientMessages(@PathVariable Long patientId) {
        return ResponseEntity.ok(contactService.getMessagesByPatient(patientId));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ContactMessage> replyToMessage(
            @PathVariable Long id,
            @RequestBody com.sliit.contact_service.dto.ReplyRequest request) {
        return ResponseEntity.ok(contactService.replyToMessage(id, request));
    }
}
