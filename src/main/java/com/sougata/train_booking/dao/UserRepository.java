package com.sougata.train_booking.dao;

import com.sougata.train_booking.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserName(String userName);
    //Optional<User> findById(String userId);
}
