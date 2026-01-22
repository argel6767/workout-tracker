package com.pxbzi.workout_tracker.exercises;

import com.pxbzi.workout_tracker.exercises.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
