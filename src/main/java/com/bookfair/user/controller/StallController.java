package com.bookfair.user.controller;

import com.bookfair.user.model.Stall;
import com.bookfair.user.repository.StallRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stalls")
@CrossOrigin
public class StallController {

    private final StallRepository stallRepository;

    public StallController(StallRepository stallRepository) {
        this.stallRepository = stallRepository;
    }

    @GetMapping
    public List<Stall> getAvailableStalls(@RequestParam(required = false) Boolean availableOnly) {
        if (availableOnly == null || availableOnly) {
            return stallRepository.findByIsReservedFalse();
        } else {
            return stallRepository.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStallById(@PathVariable Integer id) {
        Optional<Stall> stall = stallRepository.findById(id);
        return stall.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =======================
    // CREATE NEW STALL
    // =======================
    @PostMapping
    public ResponseEntity<Stall> createStall(@RequestBody Stall stall) {
        if (stall.getReserved() == null) {
            stall.setReserved(false); // Default to not reserved
        }
        Stall savedStall = stallRepository.save(stall);
        return ResponseEntity.ok(savedStall);
    }


    // =======================
    // UPDATE EXISTING STALL
    // =======================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStall(@PathVariable Integer id, @RequestBody Stall updatedStall) {
        Optional<Stall> optionalStall = stallRepository.findById(id);
        if (optionalStall.isPresent()) {
            Stall stall = optionalStall.get();
            stall.setStallCode(updatedStall.getStallCode());
            stall.setCategory(updatedStall.getCategory());
            stall.setPrice(updatedStall.getPrice());
            stall.setReserved(updatedStall.getReserved());
            stall.setLocationCoordinates(updatedStall.getLocationCoordinates());
            stallRepository.save(stall);
            return ResponseEntity.ok(stall);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // =======================
    // DELETE STALL
    // =======================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStall(@PathVariable Integer id) {
        if (stallRepository.existsById(id)) {
            stallRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // =======================
    // RESERVE A STALL
    // =======================
    @PostMapping("/{id}/reserve")
    public ResponseEntity<?> reserveStall(@PathVariable Integer id) {
        Optional<Stall> optionalStall = stallRepository.findById(id);
        if (optionalStall.isPresent()) {
            Stall stall = optionalStall.get();
            if (stall.getReserved()) {
                return ResponseEntity.badRequest().body("Stall is already reserved.");
            }
            stall.setReserved(true);
            stallRepository.save(stall);
            return ResponseEntity.ok(stall);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // =======================
    // RELEASE A STALL
    // =======================
    @PostMapping("/{id}/release")
    public ResponseEntity<?> releaseStall(@PathVariable Integer id) {
        Optional<Stall> optionalStall = stallRepository.findById(id);
        if (optionalStall.isPresent()) {
            Stall stall = optionalStall.get();
            if (!stall.getReserved()) {
                return ResponseEntity.badRequest().body("Stall is already available.");
            }
            stall.setReserved(false);
            stallRepository.save(stall);
            return ResponseEntity.ok(stall);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
