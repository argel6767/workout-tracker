import { useQuery } from "@tanstack/react-query";
import { getWorkoutsBreakdownAiAnalysis } from "../api/analytics";

export const useGetWorkoutsBreakdownAiAnalysis = () => useQuery({
  queryKey: ["workoutsBreakdownAiAnalysis"],
  queryFn: getWorkoutsBreakdownAiAnalysis,
});
