package com.pxbzi.workout_tracker.workouts;

import com.pxbzi.workout_tracker.workouts.models.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
