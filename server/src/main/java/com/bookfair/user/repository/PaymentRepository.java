package com.bookfair.user.repository;

import com.bookfair.user.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByTransactionId(String transactionId);
}
