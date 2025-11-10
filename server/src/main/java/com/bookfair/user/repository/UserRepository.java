package com.bookfair.user.repository;

import com.bookfair.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(String role);
    
    @Query("SELECT u FROM User u WHERE u.business.businessId = :businessId")
    List<User> findByBusinessId(@Param("businessId") Integer businessId);
}
