package com.sougata.train_booking.services;

import com.sougata.train_booking.dao.UserRepository;
import com.sougata.train_booking.models.authentication.AuthenticationRequest;
import com.sougata.train_booking.models.entities.User;
import com.sougata.train_booking.utility.JWTUtil;
import com.sougata.train_booking.validation.UserValidation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserValidation userValidation;

    public UserService(AuthenticationManager authenticationManager,
                       MyUserDetailsService myUserDetailsService, JWTUtil jwtUtil,
                       UserRepository userRepository, UserValidation userValidation) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userValidation = userValidation;
    }

    public boolean createUser(User user) {
        user.setActive(true);

        if (!userValidation.validateUser(user, "creation")) {
            return false;
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return true;
    }

    public Optional<User> fetchUser(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User updateUser(User newMe, String userName) {
        Optional<User> oldMe;
        User finalMe;

        if (!userName.isEmpty()) {
            oldMe = userRepository.findByUserName(userName);

            if (oldMe.isPresent()) {
                finalMe = oldMe.get();
            } else {
                return null;
            }
        } else {
            return null;
        }

        finalMe.setName(newMe.getName());

        if (!userValidation.validateUser(finalMe, "updation")) {
            return null;
        }

        return userRepository.save(finalMe);
    }

    public boolean destroyUser(String userName) {
        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            return false;
        }
        userRepository.deleteById(user.get().getId());
        return true;
    }

    public String authenticate(AuthenticationRequest authenticationRequest) {
        // method creates an authentication token and sends it back to the user

        try {
            System.out.println(authenticationRequest.getUserName() + " # " + authenticationRequest.getPassword());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUserName(), authenticationRequest.getPassword()
            ));
        } catch (BadCredentialsException e) {
            return "bad_credentials";
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUserName());
        return jwtUtil.generateToken(userDetails);
    }
}
