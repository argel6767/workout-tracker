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

    @DeleteMapping()
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
