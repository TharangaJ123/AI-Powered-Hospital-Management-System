package com.sliit.user_management.repository;

import com.sliit.user_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.doctorRegistrationNumber = :drNo")
    Optional<User> findByDoctorRegistrationNumber(@Param("drNo") String drNo);
}
