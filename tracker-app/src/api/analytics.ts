import { apiClient } from "./apiConfig"
import type { AnalyticsDto } from "../lib/analytics-dtos"

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