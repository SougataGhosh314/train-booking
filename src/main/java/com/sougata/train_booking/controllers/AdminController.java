package com.sougata.train_booking.controllers;

import com.sougata.train_booking.config.CoachConfig;
import com.sougata.train_booking.config.CoachType;
import com.sougata.train_booking.dao.*;
import com.sougata.train_booking.global.Variables;
import com.sougata.train_booking.models.entities.*;
import com.sougata.train_booking.utility.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@CrossOrigin("*")
@RestController
public class AdminController {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final DateRepository dateRepository;
    private final BookingRepository bookingRepository;
    private final SeatIdRepository seatIdRepository;

    public AdminController(JWTUtil jwtUtil, UserRepository userRepository,
                           SeatRepository seatRepository, DateRepository dateRepository, BookingRepository bookingRepository, SeatIdRepository seatIdRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.dateRepository = dateRepository;
        this.bookingRepository = bookingRepository;
        this.seatIdRepository = seatIdRepository;
    }

    @PostMapping(path = "/add_coach")
    public ResponseEntity<?> addCoach(@RequestParam String coachType, @RequestHeader("Authorization") String authToken) {
        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }

        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized");
        }
        if (!user.get().getRoles().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized!");
        }

        int newCoachNumber = Variables.incrementCoaches();

        if (coachType.equals(CoachType.AC_SLEEPER.name())) {
            List<Seat> seats = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(CoachConfig.maxCapACSleeper); i++) {
                seats.add(
                        Seat.builder()
                                .seatNo(String.valueOf(i+1))
                                .coachType(CoachType.AC_SLEEPER.name())
                                .coachNo(String.valueOf(newCoachNumber))
                                .build()
                );
            }
            seatRepository.saveAll(seats);
            return ResponseEntity.status(HttpStatus.CREATED).body(seats);
        }

        if (coachType.equals(CoachType.NON_AC_SLEEPER.name())) {
            List<Seat> seats = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(CoachConfig.maxCapNonACSleeper); i++) {
                seats.add(
                        Seat.builder()
                                .seatNo(String.valueOf(i+1))
                                .coachType(CoachType.NON_AC_SLEEPER.name())
                                .coachNo(String.valueOf(newCoachNumber))
                                .build()
                );
            }
            seatRepository.saveAll(seats);
            return ResponseEntity.status(HttpStatus.CREATED).body(seats);
        }

        if (coachType.equals(CoachType.SEATER.name())) {
            List<Seat> seats = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(CoachConfig.maxCapSeater); i++) {
                seats.add(
                        Seat.builder()
                                .seatNo(String.valueOf(i+1))
                                .coachType(CoachType.SEATER.name())
                                .coachNo(String.valueOf(newCoachNumber))
                                .build()
                );
            }
            seatRepository.saveAll(seats);
            return ResponseEntity.status(HttpStatus.CREATED).body(seats);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("bad request");
    }

    @PostMapping(path = "/remove_coach")
    public ResponseEntity<?> removeCoach(@RequestParam String coachNo, @RequestHeader("Authorization") String authToken) {
        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }

        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized");
        }
        if (!user.get().getRoles().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized!");
        }

        if (Integer.parseInt(coachNo) > 0 && Integer.parseInt(coachNo) <= Variables.getNoOfCoaches()) {
            List<Seat> allSeats = seatRepository.findAll();
            List<String> seatIdsToRemove;
            seatIdsToRemove = allSeats.stream().filter(seat -> seat.getCoachNo().equals(coachNo))
                    .map(Seat::getSeatId)
                    .collect(Collectors.toList());
            seatIdsToRemove.forEach(seatRepository::deleteById);
            return ResponseEntity.status(HttpStatus.OK).body("coach deleted");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid request!");
        }
    }

    @PostMapping(path = "/add_date")
    public ResponseEntity<?> addDate(@RequestParam String date, @RequestHeader("Authorization") String authToken) {
        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }

        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized");
        }
        if (!user.get().getRoles().contains("ROLE_ADMIN") || date.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized!");
        }

        Date dateCreated = dateRepository.save(
                Date.builder().date(date).build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dateCreated);
    }

    @GetMapping(path = "/get_all_seats")
    public ResponseEntity<?> getAllBookedSeats(
            @RequestParam(defaultValue = "booked") String query,
            @RequestHeader("Authorization") String authToken) {
        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }

        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized");
        }
        if (!user.get().getRoles().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized!");
        }

        List<Booking> bookings = bookingRepository.findAll();
        List<Seat> availableSeats = seatRepository.findAll();

        List<Seat> bookedSeats = new ArrayList<>();
        for (Booking booking: bookings) {
            List<SeatId> seatIdOBJs = seatIdRepository.findAllByBookingId(booking.getBookingId());
            List<String> seatIds = seatIdOBJs.stream().map(SeatId::getSeatId).collect(Collectors.toList());
            for (String seatId: seatIds) {
                Optional<Seat> seat = seatRepository.findById(seatId);
                seat.ifPresent(bookedSeats::add);
            }
        }

        availableSeats.removeAll(bookedSeats);

        return ResponseEntity.status(HttpStatus.OK).body(query.equals("booked") ? bookedSeats : availableSeats);
    }
}