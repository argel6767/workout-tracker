import { useQuery } from "@tanstack/react-query"
import { getExerciseAnalytics } from "../api/analytics"

export const useGetExerciseAnalytics = (exerciseId: number, numOfMonthsBack: number) => {
  return useQuery({
    queryKey: [exerciseId, numOfMonthsBack],
    queryFn: async () => {return await getExerciseAnalytics(exerciseId, numOfMonthsBack)}
  })
}