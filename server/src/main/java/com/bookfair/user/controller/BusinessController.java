package com.bookfair.user.controller;

import com.bookfair.user.model.Business;
import com.bookfair.user.repository.BusinessRepository;
import com.bookfair.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/business")
@CrossOrigin
public class BusinessController {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public BusinessController(BusinessRepository businessRepository, UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    @GetMapping()
    public Map<Integer, Boolean> getBusinesses(@RequestParam(required = false) Integer userId) {

        List<Business> businesses = businessRepository.findAll();
        Map<Integer, Boolean> result = new LinkedHashMap<>();

        if (userId == null) {
            for (Business b : businesses) {
                result.put(b.getBusinessId(), false);
            }

            return result;
        }

        Optional<Integer> userBusinessId = userRepository.findById(userId)
                .map(user -> user.getBusiness() != null ? user.getBusiness().getBusinessId() : null);

        for (Business b : businesses) {
            if (userBusinessId.isPresent() && Objects.equals(userBusinessId.get(), b.getBusinessId())) {
                result.put(b.getBusinessId(), true);
            } else {
                result.put(b.getBusinessId(), false);
            }
        }

        return result;
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<?> getBusinessById(@PathVariable Integer businessId) {
        return businessRepository.findById(businessId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Business not found")));
    }
    
    @GetMapping("/all")
    public List<Business> getAllBusinesses() {
    return businessRepository.findAll();
    }

}
