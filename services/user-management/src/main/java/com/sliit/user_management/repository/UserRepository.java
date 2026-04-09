package com.sliit.user_management.repository;

import com.sliit.user_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Doctor d WHERE d.doctorRegistrationNumber = :drNo")
    Optional<User> findByDoctorRegistrationNumber(String drNo);
}
