package com.sougata.train_booking.controllers.user;

import com.sougata.train_booking.models.entities.User;
import com.sougata.train_booking.services.UserService;
import com.sougata.train_booking.utility.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

//@CrossOrigin("*")
@RestController
public class ProfileFetchingController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public ProfileFetchingController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //@Cacheable(cacheNames = "currentUser")
    @GetMapping("/get_my_profile")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String authToken) {

        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }

        Optional<User> me = userService.fetchUser(userName);
        if (me.isPresent()) {
            return ResponseEntity.ok().body(me.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }
}
