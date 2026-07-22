import { useQuery } from "@tanstack/react-query";
import { getWeeklyOneRepMaxAiAnalysis } from "../api/analytics";

export const useGetWeeklyOneRepMaxAiAnalysis = (
  muscleId: number | undefined,
  numWeeksBack: number,
) => useQuery({
  queryKey: ["weeklyOneRepMaxAiAnalysis", muscleId, numWeeksBack],
  queryFn: () => getWeeklyOneRepMaxAiAnalysis(muscleId!, numWeeksBack),
  enabled: Boolean(muscleId) && numWeeksBack > 0,
});
