package com.sliit.user_management.repository;

import com.sliit.user_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find a user by their unique email address
    Optional<User> findByEmail(String email);

    // JPQL query to find a doctor user by their specific registration number
    @Query("SELECT u FROM User u WHERE u.doctorRegistrationNumber = :drNo")
    Optional<User> findByDoctorRegistrationNumber(@Param("drNo") String drNo);
}
