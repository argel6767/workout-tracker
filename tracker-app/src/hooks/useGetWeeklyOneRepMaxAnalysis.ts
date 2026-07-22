import { useQuery } from "@tanstack/react-query";
import { getWeeklyOneRepMaxAnalysis } from "../api/analytics";

export const useGetWeeklyOneRepMaxAnalysis = (
  muscleId: number | undefined,
  numWeeksBack: number,
) => useQuery({
  queryKey: ["weeklyOneRepMaxAnalysis", muscleId, numWeeksBack],
  queryFn: () => getWeeklyOneRepMaxAnalysis(muscleId!, numWeeksBack),
  enabled: Boolean(muscleId) && numWeeksBack > 0,
});
