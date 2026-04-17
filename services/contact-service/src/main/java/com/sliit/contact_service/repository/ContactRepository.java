package com.sliit.contact_service.repository;

import com.sliit.contact_service.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByPatientId(Long patientId);
}
