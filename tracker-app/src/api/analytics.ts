import { apiClient } from "./apiConfig"
import type { AnalyticsDto, ChatResponseDto } from "../lib/analytics-dtos"

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

export const getRelativeStrengthByExercise = async (exerciseId: number, numOfMonthsBack: number) => {
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