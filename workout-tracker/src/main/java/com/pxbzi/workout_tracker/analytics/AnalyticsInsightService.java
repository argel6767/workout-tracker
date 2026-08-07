package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.analytics.models.StrongestExercisesOverviewDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyOneRepMaxAnalysisDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyVolumeAnalysisDto;
import com.pxbzi.workout_tracker.gemini.GeminiService;
import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.weights.WeightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AnalyticsInsightService {

    private static final int AGE = 24;
    private static final String SEX = "male";

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final ExerciseProgressAnalyticsService exerciseProgressAnalyticsService;
    private final VolumeAnalyticsService volumeAnalyticsService;
    private final StrengthAnalyticsService strengthAnalyticsService;
    private final WeightService weightService;

    public ChatResponseDto analyzeExerciseProgression(Long exerciseId)
            throws JsonProcessingException {
        String data = objectMapper.writeValueAsString(
                exerciseProgressAnalyticsService.getExerciseProgression(exerciseId));
        return geminiService.getChatResponseDto(data);
    }

    public ChatResponseDto analyzeWorkoutsBreakdown() throws JsonProcessingException {
        String prompt = "Analyze this workout-count distribution by muscle group in no more than five short sentences. "
                + "Identify the most important training-balance trend and finish with one actionable recommendation. "
                + "Do not list every value or invent missing context. Data: "
                + objectMapper.writeValueAsString(volumeAnalyticsService.aggregateWorkoutsByMuscle());
        return geminiService.getConciseChatResponseDto(prompt);
    }

    public ChatResponseDto analyzeSetsBreakdown() throws JsonProcessingException {
        String prompt = "Analyze this set-count distribution by muscle group in no more than five short sentences. "
                + "Identify the most important training-volume balance trend and finish with one actionable recommendation. "
                + "Do not list every value or invent missing context. Data: "
                + objectMapper.writeValueAsString(volumeAnalyticsService.aggregateSetsByMuscle());
        return geminiService.getConciseChatResponseDto(prompt);
    }

    public ChatResponseDto analyzeWeeklyVolume(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) throws JsonProcessingException {
        WeeklyVolumeAnalysisDto analysis = volumeAnalyticsService.getWeeklyVolumeAnalysis(
                muscleId, muscleGroup, date, numWeeksBack);
        String prompt = "Summarize this week-over-week workout volume data in no more than five short sentences. "
                + "State the main trend and give one actionable recommendation. "
                + "Do not list every week, recalculate values, or invent missing data. Data: "
                + objectMapper.writeValueAsString(analysis);
        return geminiService.getConciseChatResponseDto(prompt);
    }

    public ChatResponseDto analyzeWeeklyOneRepMax(
            Long muscleId,
            LocalDate date,
            Integer numWeeksBack
    ) throws JsonProcessingException {
        WeeklyOneRepMaxAnalysisDto analysis = strengthAnalyticsService.getWeeklyOneRepMaxAnalysis(
                muscleId, date, numWeeksBack);
        String prompt = "Summarize this exercise-specific week-over-week estimated one-rep-max data in no more "
                + "than five short sentences. State the main strength trend and give one actionable recommendation. "
                + "Do not list every exercise or week. Treat missing weeks as missing data and do not invent data. Data: "
                + objectMapper.writeValueAsString(analysis);
        return geminiService.getConciseChatResponseDto(prompt);
    }

    public ChatResponseDto analyzeAllStrongestExercises(
            boolean includeMuscles,
            boolean includeMuscleGroups
    ) throws JsonProcessingException {
        StrongestExercisesOverviewDto overview = strengthAnalyticsService.getAllStrongestExercises(
                includeMuscles, includeMuscleGroups);
        String prompt = "Analyze this strongest-exercise overview in no more than five short sentences. "
                + "Summarize overall strength coverage, call out at most two notable strengths or gaps, and finish "
                + "with one actionable recommendation. Do not rank unrelated exercises against each other, provide "
                + "population comparisons, list every row, or invent missing data. User context: age " + AGE
                + ", sex " + SEX + ", latest body weight: "
                + objectMapper.writeValueAsString(weightService.getNewestWeightEntry()) + ". Data: "
                + objectMapper.writeValueAsString(overview);
        return geminiService.getConciseChatResponseDto(prompt);
    }
}
