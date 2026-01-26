package com.pxbzi.workout_tracker.workouts;

import com.pxbzi.workout_tracker.exercises.ExerciseService;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.workouts.models.NewWorkoutDto;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseService exerciseService;
    private final int PAGE_SIZE = 20;

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
    
    public List<WorkoutDto> getWorkoutsByExercise(Long exerciseId) {
        List<Workout> workouts = workoutRepository.findByExercise(exerciseId);
        return workouts.stream()
                .map(WorkoutDto::getWorkoutDto)
                .toList();
    }

    public void deleteWorkout(Long id) {
        workoutRepository.deleteById(id);
    }

    public List<WorkoutDto> getWorkoutsByDate(LocalDate date) {
        List<Workout> workouts =  workoutRepository.findByWorkoutDate(date);

        return workouts.stream()
                .map(WorkoutDto::getWorkoutDto)
                .toList();
    }

    public List<WorkoutDto> getWorkoutsBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Workout> workouts =  workoutRepository.findByWorkoutDateBetween(startDate, endDate);
        return workouts.stream()
                .map(WorkoutDto::getWorkoutDto)
                .toList();
    }

    public List<Workout> getWorkoutsByExerciseId(Long exerciseId, int numOfMonthsBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(numOfMonthsBack);
        return workoutRepository.findByExerciseAndDateRange(exerciseId, startDate, endDate);
    }

    private Workout mapWorkout(NewWorkoutDto newWorkoutDto) {
        Workout workout = new Workout();
        Exercise exercise = exerciseService.getExerciseEntity(newWorkoutDto.exerciseId());
        workout.setExercise(exercise);
        List<WorkoutSet> workoutSets = newWorkoutDto.sets().stream()
                .map(set -> new WorkoutSet(workout, set.reps(), set.weight()))
                .toList();
        workout.setWorkoutSets(workoutSets);
        return workout;
    }
}
