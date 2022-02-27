package com.sougata.train_booking.controllers;

import com.sougata.train_booking.models.entities.User;
import com.sougata.train_booking.models.authentication.AuthenticationRequest;
import com.sougata.train_booking.models.authentication.AuthenticationResponse;
import com.sougata.train_booking.services.UserService;
import com.sougata.train_booking.utility.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

//@CrossOrigin("*")
@RestController
public class RootController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public RootController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/test")
    public ResponseEntity<?> sayHello() {
        return ResponseEntity.ok("Hello");
    }

    @PostMapping(path = "/create_user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        String passwordBeforeEncryption = user.getPassword();
        boolean result = userService.createUser(user);
        if (result) {
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(user.getUserName(),
                    passwordBeforeEncryption);
            String token = userService.authenticate(authenticationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthenticationResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid input(s) for user creation!");
        }
    }

    //@CacheEvict(cacheNames = "currentUser")
    @DeleteMapping(value="/remove_user")
    @ResponseBody
    public ResponseEntity<?> removePerson(@RequestHeader("Authorization") String authToken) {

        String userName = null, jwt = null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            jwt = authToken.substring(7);
            userName = Arrays.asList(jwtUtil.extractUsername(jwt).split("_")).get(0);
        }
        boolean result = userService.destroyUser(userName);
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Your profile was deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        String result = userService.authenticate(authenticationRequest);
        if (result.equals("bad_credentials")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect userName and password!");
        } else {
            return ResponseEntity.ok().body(new AuthenticationResponse(result));
        }
    }

}