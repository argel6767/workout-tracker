package com.pxbzi.workout_tracker.exercises;

import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.exercises.models.NewExerciseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/exercises")
@Data
@AllArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping()
    public ResponseEntity<ExerciseDTO> createExercise(@RequestBody NewExerciseDto newExerciseDto) {
        ExerciseDTO exerciseDTO = exerciseService.createExercise(newExerciseDto);
        return ResponseEntity
                .created(getLocation(exerciseDTO))
                .body(exerciseDTO);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ExerciseDTO>> bulkCreateExercises(@RequestBody List<NewExerciseDto> newExerciseDtos) {
        List<ExerciseDTO> dtos = exerciseService.bulkCreateExercises(newExerciseDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExercise(@PathVariable Long id) {
        ExerciseDTO exerciseDTO = exerciseService.getExercise(id);
        return ResponseEntity.ok(exerciseDTO);
    }

    @GetMapping()
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        List<ExerciseDTO> dtos = exerciseService.getAllExercises();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(@PathVariable Long id, @RequestBody NewExerciseDto newExerciseDto) {
        ExerciseDTO exerciseDTO = exerciseService.updateExercise(id, newExerciseDto);
        return ResponseEntity.ok(exerciseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    private URI getLocation(ExerciseDTO exerciseDTO) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(exerciseDTO.id())
                .toUri();
    }
}
