package com.pxbzi.workout_tracker.workouts;

import com.pxbzi.workout_tracker.exercises.ExerciseService;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.workouts.models.NewWorkoutDto;
import com.pxbzi.workout_tracker.workouts.models.Set;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@AllArgsConstructor
@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseService exerciseService;

    public WorkoutDto createWorkout(NewWorkoutDto newWorkoutDto) {
        Workout newWorkout = workoutRepository.save(mapWorkout(newWorkoutDto));
        return WorkoutDto.getWorkoutDto(newWorkout);
    }

    public List<WorkoutDto> bulkCreateWorkouts(List<NewWorkoutDto> newWorkoutDtos) {
        List<Workout> newWorkouts = newWorkoutDtos.stream()
                .map(this::mapWorkout)
                .toList();

        return workoutRepository.saveAll(newWorkouts).stream()
                .map(WorkoutDto::getWorkoutDto)
                .toList();
    }

    public WorkoutDto getWorkout(Long id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow();
        return WorkoutDto.getWorkoutDto(workout);
    }

    public List<WorkoutDto> getAllWorkouts() {
        return workoutRepository.findAll().stream()
                .map(WorkoutDto::getWorkoutDto)
                .toList();
    }

    public void deleteWorkout(Long id) {
        workoutRepository.deleteById(id);
    }

    private Workout mapWorkout(NewWorkoutDto newWorkoutDto) {
        Workout workout = new Workout();
        Exercise exercise = exerciseService.getExerciseEntity(newWorkoutDto.exerciseId());
        workout.setExercise(exercise);
        List<Set> sets = newWorkoutDto.sets().stream()
                .map(set -> new Set(workout, set.reps(), set.weight()))
                .toList();
        workout.setSets(sets);
        return workout;
    }
}
