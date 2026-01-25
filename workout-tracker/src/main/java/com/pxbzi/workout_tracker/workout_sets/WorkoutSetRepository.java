package com.pxbzi.workout_tracker.workout_sets;

import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query("""
    SELECT s FROM WorkoutSet s
    JOIN FETCH s.workout w
    JOIN FETCH w.exercise e
    JOIN e.musclesWorked em
    JOIN em.muscle m
    WHERE m.muscleGroup = :muscleGroup
    """)
    List<WorkoutSet> findSetsByMuscleGroup(@Param("muscleGroup") MuscleGroup muscleGroup);

    @Query("""
    SELECT s FROM WorkoutSet s
    JOIN FETCH s.workout w
    JOIN FETCH w.exercise e
    JOIN e.musclesWorked em
    WHERE em.muscle.id = :muscleId
    """)
    List<WorkoutSet> findSetsByMuscleId(@Param("muscleId") Long muscleId);

    @Query("SELECT s FROM WorkoutSet s " +
            "JOIN FETCH s.workout w " +
            "WHERE w.exercise.id = :exerciseId " +
            "AND w.workoutDate BETWEEN :startDate AND :endDate " +
            "ORDER BY w.workoutDate ASC")
    List<WorkoutSet> findSetsByExerciseAndDateRange(Long exerciseId, LocalDate startDate, LocalDate endDate);
}
