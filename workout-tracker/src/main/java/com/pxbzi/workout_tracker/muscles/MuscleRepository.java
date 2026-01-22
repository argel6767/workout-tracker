package com.pxbzi.workout_tracker.muscles;

import com.pxbzi.workout_tracker.muscles.models.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuscleRepository extends JpaRepository<Muscle,Long> {
}
