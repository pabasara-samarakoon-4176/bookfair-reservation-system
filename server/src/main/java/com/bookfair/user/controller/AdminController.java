package com.bookfair.user.controller;

import com.bookfair.user.model.Reservation;
import com.bookfair.user.model.Stall;
import com.bookfair.user.repository.ReservationRepository;
import com.bookfair.user.repository.StallRepository;
import com.bookfair.user.service.EmailService;
import com.bookfair.user.service.QRCodeService;
import com.constants.StallStatuses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final ReservationRepository reservationRepository;
    private final StallRepository stallRepository;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    public AdminController(ReservationRepository reservationRepository, StallRepository stallRepository, 
                         QRCodeService qrCodeService, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.stallRepository = stallRepository;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @GetMapping("/reservations/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingReservations() {
        try {
            List<Reservation> pendingReservations = reservationRepository.findByStatus("PENDING");
            return ResponseEntity.ok(Map.of(
                    "count", pendingReservations.size(),
                    "reservations", pendingReservations
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch pending reservations: " + e.getMessage()));
        }
    }
    
    @PostMapping("/reservations/{reservationId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveReservation(@PathVariable Integer reservationId) {
        try {
            Optional<Reservation> resOpt = reservationRepository.findById(reservationId);

            if (resOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Reservation not found"));
            }

            Reservation reservation = resOpt.get();

            // Check if reservation is already approved or in another terminal state
            if (!"PENDING".equalsIgnoreCase(reservation.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Only pending reservations can be approved. Current status: " + reservation.getStatus()));
            }

            // Approve the reservation
            reservation.setStatus("APPROVED");
            reservationRepository.save(reservation);

            // Update stall status to RESERVED
            Stall stall = reservation.getStall();
            stall.setStatus(StallStatuses.RESERVED);
            stallRepository.save(stall);

            // Generate QR code if not already generated
            byte[] qrBytes = reservation.getQrCodeImage();
            if (qrBytes == null || qrBytes.length == 0) {
                try {
                    String qrData = "Reservation ID: " + reservation.getReservationId()
                            + " | User: " + reservation.getUser().getName()
                            + " | Stall: " + reservation.getStall().getStallCode()
                            + " | Status: " + reservation.getStatus();

                    final int QR_WIDTH = 250;
                    final int QR_HEIGHT = 250;

                    qrBytes = qrCodeService.generateQRCodeImage(qrData, QR_WIDTH, QR_HEIGHT);
                    reservation.setQrCodeImage(qrBytes);
                    reservationRepository.save(reservation);
                } catch (Exception e) {
                    // If QR generation fails, we'll still send the email without QR
                    qrBytes = null;
                }
            }

            // Send confirmation email with QR code to user
            String emailBody = String.format(
                "<h2>Bookfair Reservation Approved</h2>" +
                "<p>Dear %s,</p>" +
                "<p>Your reservation has been approved successfully!</p>" +
                "<p><b>Reservation ID:</b> %d<br>" +
                "   <b>Stall:</b> %s<br>" +
                "   <b>Status:</b> APPROVED</p>" +
                "<p>Your QR code for entry is attached below.</p>" +
                "<p>We look forward to seeing you at the Colombo International Bookfair!</p>",
                reservation.getUser().getName(),
                reservation.getReservationId(),
                reservation.getStall().getStallCode()
            );

            try {
                if (qrBytes != null && qrBytes.length > 0) {
                    emailService.sendReservationConfirmation(
                            reservation.getUser().getEmail(),
                            "Bookfair Reservation Approved",
                            emailBody,
                            qrBytes
                    );
                } else {
                    // Send email without attachment if QR generation failed
                    emailService.sendReservationConfirmation(
                            reservation.getUser().getEmail(),
                            "Bookfair Reservation Approved",
                            emailBody,
                            null
                    );
                }
            } catch (Exception emailException) {
                // Log the error but don't fail the approval
                System.err.println("Failed to send confirmation email: " + emailException.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Reservation approved successfully",
                    "reservationId", reservationId,
                    "stallCode", stall.getStallCode(),
                    "newStatus", stall.getStatus(),
                    "emailSent", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve reservation: " + e.getMessage()));
        }
    }
}
