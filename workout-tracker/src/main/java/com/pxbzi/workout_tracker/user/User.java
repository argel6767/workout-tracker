package com.pxbzi.workout_tracker.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import java.util.List;

import com.pxbzi.workout_tracker.weights.models.Weight;
import com.pxbzi.workout_tracker.workouts.models.Workout;

@Entity
@Data
public class User   {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Workout> workouts;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Weight> weightEntries;

    
    
}