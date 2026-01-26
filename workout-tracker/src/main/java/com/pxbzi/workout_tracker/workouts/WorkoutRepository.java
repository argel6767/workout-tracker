package com.pxbzi.workout_tracker.workouts;

import com.pxbzi.workout_tracker.workouts.models.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByWorkoutDate(LocalDate workoutDate);

    List<Workout> findByWorkoutDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT w FROM Workout w " +
            "WHERE w.exercise.id = :exerciseId")
    List<Workout> findByExercise(Long exerciseId);

    @Query("SELECT w FROM Workout w " +
            "WHERE w.exercise.id = :exerciseId " +
            "AND w.workoutDate BETWEEN :startDate AND :endDate")
    List<Workout> findByExerciseAndDateRange(Long exerciseId, LocalDate startDate, LocalDate endDate);
}
