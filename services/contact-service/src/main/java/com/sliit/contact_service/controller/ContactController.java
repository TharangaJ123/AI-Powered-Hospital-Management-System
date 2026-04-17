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
@CrossOrigin(origins = "*", allowedHeaders = "*")
@lombok.extern.slf4j.Slf4j
public class ContactController {

    private final ContactService contactService;

    // Endpoint to accept and process new contact form submissions from users
    @PostMapping("/submit")
    public ResponseEntity<ContactMessage> submitMessage(@RequestBody ContactRequest request) {
        log.info("Received contact form submission from: {}", request.getEmail());
        try {
            // Persists the incoming request data and returns the saved entity
            ContactMessage savedMessage = contactService.saveMessage(request);
            log.info("Successfully saved contact message with ID: {}", savedMessage.getId());
            return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error saving contact message: {}", e.getMessage());
            throw e;
        }
    }

    // Endpoint to retrieve all submitted inquiries for administrative viewing
    @GetMapping("/all")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    // Endpoint to fetch message history for a specific registered patient
    @GetMapping("/user/{patientId}")
    public ResponseEntity<List<ContactMessage>> getPatientMessages(@PathVariable Long patientId) {
        return ResponseEntity.ok(contactService.getMessagesByPatient(patientId));
    }

    // Endpoint for administrators to provide a response to a specific inquiry
    @PostMapping("/{id}/reply")
    public ResponseEntity<ContactMessage> replyToMessage(
            @PathVariable Long id,
            @RequestBody com.sliit.contact_service.dto.ReplyRequest request) {
        return ResponseEntity.ok(contactService.replyToMessage(id, request));
    }
}
