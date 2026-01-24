import { useQuery } from "@tanstack/react-query"
import { getMuscles } from "../api/muscles"

export const useGetMuscles = () => {
  return useQuery({
    queryKey: ['muscles'],
    queryFn: async () => {
      return await getMuscles();
    }
  })
}