package com.sougata.train_booking.dao;

import com.sougata.train_booking.models.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findAllByCoachNo(String coachNo);
}
