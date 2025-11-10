package com.bookfair.user.service;

import com.bookfair.user.model.Stall;
import com.bookfair.user.model.Business;
import com.bookfair.user.repository.StallRepository;
import com.bookfair.user.repository.BusinessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StallService {

    private final StallRepository stallRepository;
    private final BusinessRepository businessRepository;

    public StallService(StallRepository stallRepository, BusinessRepository businessRepository) {
        this.stallRepository = stallRepository;
        this.businessRepository = businessRepository;
    }

    public List<Stall> getAllStalls() {
        return stallRepository.findAll();
    }

    public Optional<Stall> getStallById(Integer id) {
        return stallRepository.findById(id);
    }

    public Stall createStall(Stall stall) {
        return stallRepository.save(stall);
    }
   
}
