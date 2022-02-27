package com.sougata.train_booking.dao;

import com.sougata.train_booking.models.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findAllByDateId(String dateId);
}
