package com.sliit.contact_service.service;

import com.sliit.contact_service.dto.ContactRequest;
import com.sliit.contact_service.model.ContactMessage;
import com.sliit.contact_service.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    // Creates and persists a new user inquiry with a default 'PENDING' status
    public ContactMessage saveMessage(ContactRequest request) {
        ContactMessage message = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .patientId(request.getPatientId())
                .status("PENDING")
                .build();
        
        return contactRepository.save(message);
    }

    // Retrieves all contact messages stored in the system for administrative review
    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll();
    }

    // Fetches all inquiries associated with a specific registered patient
    public List<ContactMessage> getMessagesByPatient(Long patientId) {
        return contactRepository.findByPatientId(patientId);
    }

    // Processes an administrator's response to an inquiry and updates its status to 'REPLIED'
    public ContactMessage replyToMessage(Long id, com.sliit.contact_service.dto.ReplyRequest request) {
        ContactMessage message = contactRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact message not found"));
        
        message.setAdminReply(request.getAdminReply());
        message.setStatus("REPLIED");
        message.setRepliedAt(LocalDateTime.now());
        
        return contactRepository.save(message);
    }
}
