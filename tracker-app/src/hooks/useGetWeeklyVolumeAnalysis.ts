import { useQuery } from "@tanstack/react-query";
import { getWeeklyVolumeAnalysis } from "../api/analytics";
import type { MuscleGroup } from "../lib/form-dtos";

export const useGetWeeklyVolumeAnalysis = (
  muscleGroup: MuscleGroup,
  muscleId: number | undefined,
  numWeeksBack: number,
) => {
  return useQuery({
    queryKey: ["weeklyVolumeAnalysis", muscleGroup, muscleId, numWeeksBack],
    queryFn: () => getWeeklyVolumeAnalysis(muscleGroup, muscleId, numWeeksBack),
    enabled: numWeeksBack > 0,
  });
};
