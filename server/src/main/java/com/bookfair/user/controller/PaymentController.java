package com.bookfair.user.controller;

import com.bookfair.user.model.Payment;
import com.bookfair.user.model.User;
import com.bookfair.user.repository.PaymentRepository;
import com.bookfair.user.repository.UserRepository;
import com.bookfair.user.service.BraintreePaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BraintreePaymentService braintreePaymentService;

    public PaymentController(PaymentRepository paymentRepository, UserRepository userRepository, BraintreePaymentService braintreePaymentService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.braintreePaymentService = braintreePaymentService;
    }

    @PostMapping
    public ResponseEntity<?> initiatePayment(@RequestBody Map<String, Object> payload) {

        Integer userId = (Integer) payload.get("userId");
        double amount = Double.parseDouble(payload.get("amount").toString());
        String method = (String) payload.get("paymentMethod");
        String nonce = (String) payload.get("nonce");

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
        User user = userOpt.get();

        String braintreeCustomerId;
        try {
            braintreeCustomerId = braintreePaymentService.findOrCreateCustomer(user, nonce);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment preparation failed: " + e.getMessage()));
        }

        String paymentResult = braintreePaymentService.processPaymentWithCustomer(
                BigDecimal.valueOf(amount),
                braintreeCustomerId
        );

        String status;
        String transactionId;
        String message;

        if (paymentResult.startsWith("SUCCESS:")) {
            status = "SUCCESS";
            transactionId = paymentResult.substring("SUCCESS: ".length()).trim();
            message = "Payment completed successfully via Braintree and vaulted.";
        } else {
            status = "FAILED";
            transactionId = "FAIL-" + System.currentTimeMillis();
            message = "Payment failed: " + paymentResult.substring("FAILED: ".length()).trim();
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentMethod(method);
        payment.setTransactionId(transactionId);
        payment.setStatus(status);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        if ("SUCCESS".equals(status)) {
            return ResponseEntity.ok(Map.of(
                    "message", message,
                    "transactionId", transactionId,
                    "status", status
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", message,
                    "transactionId", transactionId,
                    "status", status
            ));
        }
    }

    @GetMapping("/status/{transactionId}")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable String transactionId) {
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionId);

        if (paymentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Payment not found"));
        }

        Payment payment = paymentOpt.get();

        return ResponseEntity.ok(Map.of(
                "transactionId", payment.getTransactionId(),
                "status", payment.getStatus(),
                "amount", payment.getAmount(),
                "method", payment.getPaymentMethod(),
                "paidAt", payment.getPaidAt()
        ));
    }
}
