import { useQuery } from "@tanstack/react-query";
import { getStrongestExercisesAiAnalysis } from "../api/analytics";

export const useGetStrongestExercisesAiAnalysis = () => useQuery({
  queryKey: ["strongestExercisesAiAnalysis"],
  queryFn: getStrongestExercisesAiAnalysis,
});
