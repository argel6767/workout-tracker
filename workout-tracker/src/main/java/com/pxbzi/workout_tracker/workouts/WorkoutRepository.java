package com.pxbzi.workout_tracker.workouts;

import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByWorkoutDate(LocalDate workoutDate);

    List<Workout> findByWorkoutDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT w FROM Workout w JOIN w.exercise e JOIN e.musclesWorked em " +
            "JOIN em.muscle m WHERE m.id = :muscleId " +
            "AND w.workoutDate BETWEEN :startDate AND :endDate ORDER BY w.workoutDate ASC")
    List<Workout> findByMuscleAndDateRange(@Param("muscleId") Long muscleId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT w FROM Workout w JOIN w.exercise e JOIN e.musclesWorked em " +
            "JOIN em.muscle m WHERE m.muscleGroup = :muscleGroup " +
            "AND w.workoutDate BETWEEN :startDate AND :endDate ORDER BY w.workoutDate ASC")
    List<Workout> findByMuscleGroupAndDateRange(@Param("muscleGroup") MuscleGroup muscleGroup,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT w FROM Workout w " +
            "WHERE w.exercise.id = :exerciseId " +
            "ORDER BY w.workoutDate DESC")
    List<Workout> findByExercise(Long exerciseId);

    @Query("SELECT w FROM Workout w " +
            "WHERE w.exercise.id = :exerciseId " +
            "AND w.workoutDate BETWEEN :startDate AND :endDate " +
            "ORDER BY w.workoutDate ASC")
    List<Workout> findByExerciseAndDateRange(Long exerciseId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT w FROM Workout w " +
            "WHERE w.exercise.id = :exerciseId " +
            "ORDER BY w.workoutDate DESC LIMIT 1")
    Optional<Workout> findNewestByExerciseId(Long exerciseId);
}
