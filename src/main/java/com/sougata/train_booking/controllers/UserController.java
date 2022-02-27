package com.sougata.train_booking.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sougata.train_booking.dao.*;
import com.sougata.train_booking.global.Variables;
import com.sougata.train_booking.models.aggregate.BookingSeatList;
import com.sougata.train_booking.models.entities.*;
import com.sougata.train_booking.services.UserService;
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
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final DateRepository dateRepository;
    private final BookingRepository bookingRepository;
    private final SeatIdRepository seatIdRepository;

    public UserController(UserService userService, JWTUtil jwtUtil, UserRepository userRepository,
                          SeatRepository seatRepository, DateRepository dateRepository, BookingRepository bookingRepository, SeatIdRepository seatIdRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.dateRepository = dateRepository;
        this.bookingRepository = bookingRepository;
        this.seatIdRepository = seatIdRepository;
    }

    @GetMapping(path = "/get_all_dates")
    public ResponseEntity<?> getDates(@RequestHeader("Authorization") String authToken) {
        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }

        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized");
        }

        return ResponseEntity.status(HttpStatus.OK).body(dateRepository.findAll());
    }

    @GetMapping(path = "/get_avail_seats_in_coach")
    public ResponseEntity<?> getSeats(@RequestParam String coachNo,
                                      @RequestParam String date,
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

        Optional<Date> dateEntry = dateRepository.findByDate(date);
        if (!dateEntry.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("date does not exist");
        }

        if (Integer.parseInt(coachNo) > Variables.getNoOfCoaches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("coachNo does not exist");
        }

        List<Booking> bookings = bookingRepository.findAllByDateId(dateEntry.get().getDateId());

        List<Seat> bookedSeatsOnDate = new ArrayList<>();
        for (Booking booking: bookings) {
            List<SeatId> seatIdOBJs = seatIdRepository.findAllByBookingId(booking.getBookingId());
            List<String> seatIds = seatIdOBJs.stream().map(SeatId::getSeatId).collect(Collectors.toList());
            for (String seatId: seatIds) {
                Optional<Seat> seat = seatRepository.findById(seatId);
                seat.ifPresent(bookedSeatsOnDate::add);
            }
        }

        List<Seat> seatsInCoach = seatRepository.findAllByCoachNo(coachNo);
        List<Seat> avail_seats_in_coach = seatsInCoach.stream().filter(seat -> !bookedSeatsOnDate.contains(seat))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(avail_seats_in_coach);
    }

    @PostMapping(path = "/book_seats")
    public ResponseEntity<?> bookSeats(@RequestBody List<String> seatIdsToBook,
                                       @RequestParam String date,
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

        Optional<Date> dateEntry = dateRepository.findByDate(date);
        if (!dateEntry.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("date does not exist");
        }

        List<Seat> seatsDemanded = new ArrayList<>();
        String coachNo = null;
        for (String seatId: seatIdsToBook) {
            Optional<Seat> seat = seatRepository.findById(seatId);
            if (seat.isPresent()) {
                if (coachNo == null) {
                    coachNo = seat.get().getCoachNo();
                } else {
                    if (!coachNo.equals(seat.get().getCoachNo())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seats should be on the same coach");
                    }
                }
                seatsDemanded.add(seat.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some seat(s) requested do not exist");
            }
        }

        List<Booking> bookings = bookingRepository.findAllByDateId(dateEntry.get().getDateId());

        List<Seat> bookedSeatsOnDate = new ArrayList<>();
        for (Booking booking: bookings) {
            List<SeatId> seatIdOBJs = seatIdRepository.findAllByBookingId(booking.getBookingId());
            List<String> seatIds = seatIdOBJs.stream().map(SeatId::getSeatId).collect(Collectors.toList());
            for (String seatId: seatIds) {
                Optional<Seat> seat = seatRepository.findById(seatId);
                seat.ifPresent(bookedSeatsOnDate::add);
            }
        }

        List<Seat> seatsInCoach = seatRepository.findAllByCoachNo(coachNo);
        List<Seat> avail_seats_in_coach = seatsInCoach.stream().filter(seat -> !bookedSeatsOnDate.contains(seat))
                .collect(Collectors.toList());

        List<String> seatIdsDemanded = seatsDemanded.stream().map(Seat::getSeatId).collect(Collectors.toList());
        List<String> seatIdsAvailable = avail_seats_in_coach.stream().map(Seat::getSeatId).collect(Collectors.toList());

        for (String seatId: seatIdsDemanded) {
            if (!seatIdsAvailable.contains(seatId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some seat(s) are not available," +
                        " cannot proceed with booking.");
            }
        }

        Booking booking = bookingRepository.save(
                Booking.builder()
                        .dateId(dateEntry.get().getDateId())
                        .userId(user.get().getId())
                        .status("CONFIRMED")
                        .build()
        );

        List<SeatId> seatIds = new ArrayList<>();
        for (String seatId: seatIdsDemanded) {
            seatIds.add(new SeatId(booking.getBookingId(), seatId));
        }
        List<SeatId> savedSeatIds = seatIdRepository.saveAll(seatIds);


        return ResponseEntity.status(HttpStatus.OK).body(new BookingSeatList(booking, savedSeatIds));
    }

}