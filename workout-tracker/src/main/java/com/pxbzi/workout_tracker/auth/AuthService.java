package com.pxbzi.workout_tracker.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pxbzi.workout_tracker.auth.models.AuthenticationDto;
import com.pxbzi.workout_tracker.auth.models.RegistrationDto;
import com.pxbzi.workout_tracker.user.UserDto;
import com.pxbzi.workout_tracker.user.UserService;
import com.pxbzi.workout_tracker.user.models.NewUserDto;
import com.pxbzi.workout_tracker.user.models.User;

import lombok.Data;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@Data
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    public UserDto registerUser(RegistrationDto registrationDto) {
        if (userService.isUserDetailsTaken(registrationDto.username(), registrationDto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email already taken");
        }
        
        String hashedPassword = passwordEncoder.encode(registrationDto.password());
        NewUserDto dto = new NewUserDto(registrationDto.username(), registrationDto.email(), hashedPassword);
        return userService.createUser(dto);
    }
    
    public UserDto authenticateUser(AuthenticationDto authenticationDto) {
        User user = userService.getUserEntry(authenticationDto.username());
        
        if (!passwordEncoder.matches(authenticationDto.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        
        return UserDto.getUserDto(user);
    }
}