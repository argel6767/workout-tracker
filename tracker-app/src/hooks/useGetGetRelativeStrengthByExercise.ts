import { useQuery } from "@tanstack/react-query";
import { getRelativeStrengthByExercise } from "../api/analytics";

export const useGetRelativeStrengthByExercise = (exerciseId: number, numOfMonthsBack: number) => {
  return useQuery({
    queryKey: [exerciseId, numOfMonthsBack],
    queryFn: async () => {return await getRelativeStrengthByExercise(exerciseId, numOfMonthsBack)}
  })
}
