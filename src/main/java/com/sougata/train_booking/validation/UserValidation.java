package com.sougata.train_booking.validation;

import com.sougata.train_booking.dao.UserRepository;
import com.sougata.train_booking.models.entities.User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class UserValidation {

    private final UserRepository userRepository;

    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean validateUser(User user, String validationFor) {

        if (validationFor.equals("creation")) {
            Optional<User> check = userRepository.findByUserName(user.getUserName());
            if (check.isPresent()) {
                System.out.println("username exists already.");
                return false;
            }
        }

        if ( !(validationFor.equals("creation") || validationFor.equals("updation")) )  return false;

        if (user.getName().equals("")) return false;
        if (Arrays.asList(user.getName().split(" ")).size() <= 1)  return false;

        String regex = "";
//        regex = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        regex = "^(?=.{8,20}$)(?![.])(?!.*[_.]{2})[a-zA-Z0-9.]+(?<![.])$";
        // userName validation:
//        ^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$
//         └─────┬────┘└───┬──┘└─────┬─────┘└─────┬─────┘ └───┬───┘
//               │         │         │            │           no _ or . at the end
//               │         │         │            │
//               │         │         │            allowed characters
//               │         │         │
//               │         │         no __ or _. or ._ or .. inside
//               │         │
//               │         no _ or . at the beginning
//               │
//                username is 8-20 characters long

        if (!user.getUserName().matches(regex))  return false;


        regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$";
//        ^ represents starting character of the string.
//        (?=.*[0-9]) represents a digit must occur at least once.
//        (?=.*[a-z]) represents a lower case alphabet must occur at least once.
//        (?=.*[A-Z]) represents an upper case alphabet that must occur at least once.
//        (?=.*[@#$%^&-+=()] represents a special character that must occur at least once.
//        (?=\\S+$) white spaces don’t allowed in the entire string.
//        .{8, 20} represents at least 8 characters and at most 20 characters.
//                $ represents the end of the string.

        if (!validationFor.equals("updation")) {
            if (!user.getPassword().matches(regex))
                return false;
        }

        if (user.getRoles().equals("ROLE_USER") || user.getRoles().equals("ROLE_ADMIN")) {
            return true;
        } else {
            return false;
        }
    }

}
