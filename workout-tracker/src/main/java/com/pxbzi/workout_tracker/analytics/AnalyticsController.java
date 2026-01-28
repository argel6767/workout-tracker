package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pxbzi.workout_tracker.analytics.models.AnalyticsDto;
import com.pxbzi.workout_tracker.analytics.models.RelativeStrengthDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleGroupDto;
import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/analytics")
@Data
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/progress/exercise")
    public AnalyticsDto getWorkoutAnalyticsByExerciseId(@RequestParam Long exerciseId, @RequestParam Integer numOfMonthsBack) {
        return analyticsService.getWorkoutAnalyticsByExerciseId(exerciseId, numOfMonthsBack);
    }

    @GetMapping("/progress/relative-strength")
    public List<RelativeStrengthDto> getRelativeStrengthByExerciseId( @RequestParam Integer numOfMonthsBack, @RequestParam Long exerciseId) {
        return analyticsService.getRelativeStrength(numOfMonthsBack, exerciseId);
    }

    @GetMapping("/progress/ai-analysis")
    public ChatResponseDto getAiAnalysisByExerciseId(@RequestParam Long exerciseId) throws JsonProcessingException {
        return analyticsService.analyzeExerciseProgression(exerciseId);
    }

    @GetMapping("/strongest-exercises/muscle-groups/{muscleGroup}")
    public StrongestExerciseByMuscleGroupDto getStrongestExerciseByMuscleGroup(@PathVariable MuscleGroup muscleGroup) {
        return analyticsService.getStrongestExerciseByMuscleGroup(muscleGroup);
    }

    @GetMapping("/strongest-exercises/muscles/{muscleId}")
    public StrongestExerciseByMuscleDto getStrongestExerciseByMuscle(@PathVariable Long muscleId) {
        return analyticsService.getStrongestExerciseByMuscle(muscleId);
    }
}
