import { apiClient } from "./apiConfig"
import type { DataPoint, AnalyticsDto, ChatResponseDto, RelativeStrengthDto, WeeklyOneRepMaxAnalysisDto, WeeklyVolumeAnalysisDto } from "../lib/analytics-dtos"
import type { MuscleGroup } from "../lib/form-dtos"

const V1_ANALYTICS = "/v1/analytics"

export const getExerciseAnalytics = async (exerciseId: number, numOfMonthsBack: number): Promise<AnalyticsDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/exercise`,
    {
      params: {
        exerciseId,
        numOfMonthsBack
      }
    })
  return response.data
}

export const getRelativeStrengthByExercise = async (exerciseId: number, numOfMonthsBack: number): Promise<RelativeStrengthDto[]> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/relative-strength`,
    {
      params: {
        exerciseId,
        numOfMonthsBack
      }
    })
  return response.data
}

export const getAiAnalysisByExercise = async (exerciseId: number): Promise<ChatResponseDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/ai-analysis`,
    {
      params: {
        exerciseId
      }
    })
  return response.data
}

export const getWorkoutsByMuscleGroup = async (): Promise<DataPoint<string, number>[]> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/workouts-breakdown`)
  return response.data
}

export const getSetsByMuscleGroup = async (): Promise<DataPoint<string, number>[]> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/sets-breakdown`)
  return response.data
}

export const getWorkoutsBreakdownAiAnalysis = async (): Promise<ChatResponseDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/workouts-breakdown/ai-analysis`);
  return response.data;
}

export const getSetsBreakdownAiAnalysis = async (): Promise<ChatResponseDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/sets-breakdown/ai-analysis`);
  return response.data;
}

export const getWeeklyVolumeAnalysis = async (
  muscleGroup: MuscleGroup,
  muscleId?: number,
  numWeeksBack = 5,
  date = new Date().toLocaleDateString("en-CA"),
): Promise<WeeklyVolumeAnalysisDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/weekly-volume`, {
    params: {
      ...(muscleId ? { muscleId } : { muscleGroup }),
      date,
      numWeeksBack,
    },
  });
  return response.data;
}

export const getWeeklyVolumeAiAnalysis = async (
  muscleGroup: MuscleGroup,
  muscleId?: number,
  numWeeksBack = 5,
  date = new Date().toLocaleDateString("en-CA"),
): Promise<ChatResponseDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/weekly-volume/ai-analysis`, {
    params: {
      ...(muscleId ? { muscleId } : { muscleGroup }),
      date,
      numWeeksBack,
    },
  });
  return response.data;
}

export const getWeeklyOneRepMaxAnalysis = async (
  muscleId: number,
  numWeeksBack = 5,
  date = new Date().toLocaleDateString("en-CA"),
): Promise<WeeklyOneRepMaxAnalysisDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/weekly-one-rep-max`, {
    params: { muscleId, date, numWeeksBack },
  });
  return response.data;
}

export const getWeeklyOneRepMaxAiAnalysis = async (
  muscleId: number,
  numWeeksBack = 5,
  date = new Date().toLocaleDateString("en-CA"),
): Promise<ChatResponseDto> => {
  const response = await apiClient.get(`${V1_ANALYTICS}/progress/weekly-one-rep-max/ai-analysis`, {
    params: { muscleId, date, numWeeksBack },
  });
  return response.data;
}
