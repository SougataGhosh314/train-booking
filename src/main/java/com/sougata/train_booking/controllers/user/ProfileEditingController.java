package com.sougata.train_booking.controllers.user;

import com.sougata.train_booking.models.entities.User;
import com.sougata.train_booking.services.UserService;
import com.sougata.train_booking.utility.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@CrossOrigin("*")
@RestController
public class ProfileEditingController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public ProfileEditingController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //@CachePut(cacheNames = "currentUser", key = "#newMe")
    //@CachePut(cacheNames = "currentUser")
    @PutMapping("/edit_my_profile")
    public ResponseEntity<?> editMyProfile(@RequestBody User newMe, @RequestHeader("Authorization") String authToken) {

        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }

        User afterUpdate = userService.updateUser(newMe, userName);
        if (afterUpdate != null) {
            return ResponseEntity.status(HttpStatus.OK).body(afterUpdate);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Your request could not be processed. This could be because: " +
                            "1. User was not found or, " +
                            "2. You do not have sufficient privileges to perform the operation or, " +
                            "3. Invalid data provided for updating user!");
        }
    }
}
