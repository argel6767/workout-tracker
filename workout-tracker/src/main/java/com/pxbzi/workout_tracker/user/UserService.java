package com.pxbzi.workout_tracker.user;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pxbzi.workout_tracker.user.models.NewUserDto;
import com.pxbzi.workout_tracker.user.models.User;
import com.pxbzi.workout_tracker.weights.WeightRepository;
import com.pxbzi.workout_tracker.workouts.WorkoutRepository;

import lombok.Data;
import java.util.List;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.weights.models.Weight;

@Service
@Data
public class UserService {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final WeightRepository weightRepository;
    
    public UserDto createUser(NewUserDto newUserDto) {
        User user = User.builder()
                .email(newUserDto.email())
                .username(newUserDto.username())
                .password(newUserDto.hashedPassword())
                .build();
        return UserDto.getUserDto(userRepository.save(user));
    }
    
    public void deleteUser(Long userId, Long authenticatedId) {
        if (!userId.equals(authenticatedId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete another user");
        }
        userRepository.deleteById(userId);
    }
    
    public boolean isUserDetailsTaken(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).isPresent();
    }
    
    public User getUserEntry(String username) {
        return userRepository.findByUsername(username)
        .orElseThrow();
    }
    
    public UserDto getUser(Long userId, Long authenticatedId) {
        if (!userId.equals(authenticatedId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot get another user's details");
        }
        return UserDto.getUserDto(userRepository.findById(userId)
            .orElseThrow());
    }
    
    public void setAllWeightsAndWorkouts(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Weight> weights = weightRepository.findAll();
        List<Workout> workouts = workoutRepository.findAll();
        
        user.getWeightEntries().addAll(weights);
        user.getWorkouts().addAll(workouts);
        userRepository.save(user);
        
    }
}