package com.pxbzi.workout_tracker.workouts;

import com.pxbzi.workout_tracker.workouts.models.NewWorkoutDto;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/workouts")
@Data
@AllArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping()
    public ResponseEntity<WorkoutDto> createWorkout(@RequestBody NewWorkoutDto newWorkoutDto) {
        WorkoutDto dto =  workoutService.createWorkout(newWorkoutDto);
        return ResponseEntity.created(getLocation(dto)).body(dto);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<WorkoutDto>> createWorkouts(@RequestBody List<NewWorkoutDto> newWorkoutDtos) {
        List<WorkoutDto> dtos = workoutService.bulkCreateWorkouts(newWorkoutDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
    }
    
    @GetMapping("/dates")
    public ResponseEntity<List<WorkoutDto>> getWorkoutsByDate(@RequestParam LocalDate date) {
        List<WorkoutDto> dtos = workoutService.getWorkoutsByDate(date);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/exercises")
    public ResponseEntity<List<WorkoutDto>> getWorkoutsByExercise(@RequestParam Long exerciseId) {
        List<WorkoutDto> dtos = workoutService.getWorkoutsByExercise(exerciseId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDto> getWorkoutById(@PathVariable Long id) {
        WorkoutDto dto = workoutService.getWorkout(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping()
    public ResponseEntity<List<WorkoutDto>> getAllWorkouts() {
        List<WorkoutDto> dtos = workoutService.getAllWorkouts();
        return ResponseEntity.ok(dtos);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutDto> updateWorkout(@PathVariable Long id, @RequestBody WorkoutDto workoutDto) {
        WorkoutDto updatedDto = workoutService.updateWorkout(id, workoutDto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WorkoutDto> deleteWorkoutById(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    private URI getLocation(WorkoutDto workoutDto) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(workoutDto.id())
                .toUri();
    }
}
