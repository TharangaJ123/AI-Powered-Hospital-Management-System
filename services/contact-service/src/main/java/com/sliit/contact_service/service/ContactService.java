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

    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll();
    }

    public List<ContactMessage> getMessagesByPatient(Long patientId) {
        return contactRepository.findByPatientId(patientId);
    }

    public ContactMessage replyToMessage(Long id, com.sliit.contact_service.dto.ReplyRequest request) {
        ContactMessage message = contactRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact message not found"));
        
        message.setAdminReply(request.getAdminReply());
        message.setStatus("REPLIED");
        message.setRepliedAt(LocalDateTime.now());
        
        return contactRepository.save(message);
    }
}
