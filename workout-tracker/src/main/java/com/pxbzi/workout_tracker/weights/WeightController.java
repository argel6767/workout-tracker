package com.pxbzi.workout_tracker.weights;

import com.pxbzi.workout_tracker.weights.models.NewWeightDto;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/weights")
@Data
public class WeightController {

    private final WeightService weightService;

    @PostMapping()
    public WeightDto createWeight(@Valid @RequestBody NewWeightDto weightDto) {
        return weightService.createWeight(weightDto);
    }
    
    @GetMapping()
    public List<WeightDto> getAllWeights() {
        return weightService.getAllWeights();
    }

    @GetMapping("/{id}")
    public WeightDto getWeight(@PathVariable Long id) {
        return weightService.getWeight(id);
    }

    @GetMapping("/dates")
    public List<WeightDto> getWeightsByDate(@RequestParam Integer numMonthsBack) {
        return weightService.getAllWeightsInDateRange(numMonthsBack);
    }

    @PutMapping("/{id}")
    public WeightDto updateWeight(@PathVariable Long id, @RequestBody WeightDto weightDto) {
        return weightService.updateWeight(id, weightDto);
    }

    @DeleteMapping("/{id}")
    public void deleteWeight(@PathVariable Long id) {
        weightService.deleteWeight(id);
    }
}
