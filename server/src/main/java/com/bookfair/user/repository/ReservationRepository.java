package com.bookfair.user.repository;

import com.bookfair.user.model.Reservation;
import com.bookfair.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByStatus(String status);
}
