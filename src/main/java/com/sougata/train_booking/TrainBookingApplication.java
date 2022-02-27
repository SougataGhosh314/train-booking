package com.sougata.train_booking;

import com.sougata.train_booking.dao.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class TrainBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainBookingApplication.class, args);
	}

}
