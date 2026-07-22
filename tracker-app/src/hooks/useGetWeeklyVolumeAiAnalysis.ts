import { useQuery } from "@tanstack/react-query";
import { getWeeklyVolumeAiAnalysis } from "../api/analytics";
import type { MuscleGroup } from "../lib/form-dtos";

export const useGetWeeklyVolumeAiAnalysis = (
  muscleGroup: MuscleGroup,
  muscleId: number | undefined,
  numWeeksBack: number,
) => {
  return useQuery({
    queryKey: ["weeklyVolumeAiAnalysis", muscleGroup, muscleId, numWeeksBack],
    queryFn: () => getWeeklyVolumeAiAnalysis(muscleGroup, muscleId, numWeeksBack),
    enabled: numWeeksBack > 0,
  });
};
