package com.bookfair.user.controller;

import com.bookfair.user.model.Business;
import com.bookfair.user.model.Stall;
import com.bookfair.user.repository.StallRepository;
import com.bookfair.user.service.StallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stalls")
@CrossOrigin
public class StallController {

    private final StallService stallService;

    private final StallRepository stallRepository;

    public StallController(StallRepository stallRepository, StallService stallService) {
        this.stallRepository = stallRepository;
        this.stallService = stallService;
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

    @GetMapping("/all")
    public List<Stall> getAllStalls() {    
    return this.stallService.getAllStalls();
    }
}
