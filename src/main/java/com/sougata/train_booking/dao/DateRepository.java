package com.sougata.train_booking.dao;

import com.sougata.train_booking.models.entities.Date;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DateRepository extends JpaRepository<Date, String> {
    Optional<Date> findByDate(String date);
}
