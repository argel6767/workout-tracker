package com.pxbzi.workout_tracker.user;

import org.springframework.stereotype.Repository;

import com.pxbzi.workout_tracker.user.models.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);

}