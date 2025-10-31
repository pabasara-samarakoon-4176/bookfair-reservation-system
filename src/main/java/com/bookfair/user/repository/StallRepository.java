package com.bookfair.user.repository;

import com.bookfair.user.model.Stall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StallRepository extends JpaRepository<Stall, Integer> {

    List<Stall> findByIsReservedFalse();
}
