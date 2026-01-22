package com.pxbzi.workout_tracker.muscles;

import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.muscles.models.NewMuscleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/muscles")
@Data
@AllArgsConstructor
public class MuscleController {

    private final MuscleService muscleService;

    @PostMapping()
    public ResponseEntity<MuscleDto> createMuscle(@RequestBody NewMuscleDto newMuscleDto) {
        MuscleDto dto = muscleService.createMuscle(newMuscleDto);
        return ResponseEntity
                .created(getLocation(dto))
                .body(dto);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<MuscleDto>> bulkCreateMuscles(@RequestBody List<NewMuscleDto> newMuscleDtos) {
        List<MuscleDto> dtos = muscleService.bulkCreateMuscles(newMuscleDtos);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MuscleDto> getMuscle(@PathVariable Long id) {
        MuscleDto dto = muscleService.getMuscle(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping()
    public ResponseEntity<List<MuscleDto>> getAllMuscles() {
        List<MuscleDto>  dtos = muscleService.getAllMuscles();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MuscleDto> updateMuscle(@PathVariable Long id, @RequestBody MuscleDto muscleDto) {
        MuscleDto dto = muscleService.updateMuscle(id, muscleDto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMuscle(@PathVariable Long id) {
        muscleService.deleteMuscleById(id);
        return ResponseEntity.noContent().build();
    }

    private URI getLocation(MuscleDto muscleDto) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(muscleDto.id())
                .toUri();
    }
}
