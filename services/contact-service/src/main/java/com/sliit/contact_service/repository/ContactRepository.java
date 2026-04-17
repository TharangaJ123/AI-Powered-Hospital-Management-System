package com.sliit.contact_service.repository;

import com.sliit.contact_service.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Data access layer for managing contact message entities in the database
public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
    // Retrieves a list of all inquiries submitted by a specific patient
    List<ContactMessage> findByPatientId(Long patientId);
}
