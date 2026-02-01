package com.pxbzi.workout_tracker.workout_sets;

import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class WorkoutSetService {

    private WorkoutSetRepository workoutSetRepository;

    public List<WorkoutSet> getSetsByMuscleGroup(MuscleGroup muscleGroup) {
        return workoutSetRepository.findSetsByMuscleGroup(muscleGroup);
    }

    public List<WorkoutSet> getSetsByMuscleId(Long muscleId) {
        return workoutSetRepository.findSetsByMuscleId(muscleId);
    }
}
