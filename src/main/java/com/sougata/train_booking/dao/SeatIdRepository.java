package com.sougata.train_booking.dao;

import com.sougata.train_booking.models.entities.SeatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatIdRepository extends JpaRepository<SeatId, String> {
    List<SeatId> findAllByBookingId(String bookingId);
}
