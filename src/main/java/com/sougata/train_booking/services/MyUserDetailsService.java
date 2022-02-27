package com.sougata.train_booking.services;

import com.sougata.train_booking.dao.UserRepository;
import com.sougata.train_booking.models.authentication.MyUserDetails;
import com.sougata.train_booking.models.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("not found: " + userName));
        return user.map(MyUserDetails::new).get();
    }

    public String loadUserIdByUserName(String userName) {
        Optional<User> user;
        try {
            user = userRepository.findByUserName(userName);
            if (user.isPresent()) {
                return user.get().getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
