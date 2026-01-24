import { useQuery } from "@tanstack/react-query"
import { getExercises } from "../api/exercise"

export const useGetExercises = () => {
  return useQuery({
    queryKey: ['exercises'],
    queryFn: async () => {
      return await getExercises();
    }
  })
}