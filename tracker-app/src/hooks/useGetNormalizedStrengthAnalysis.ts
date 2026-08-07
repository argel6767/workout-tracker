import { useQuery } from "@tanstack/react-query";
import { getNormalizedStrengthAnalysis } from "../api/analytics";
import type { MuscleGroup } from "../lib/form-dtos";

export const useGetNormalizedStrengthAnalysis = (
  muscleGroup: MuscleGroup,
  muscleId: number | undefined,
  numWeeksBack: number,
) => {
  return useQuery({
    queryKey: ["normalizedStrengthAnalysis", muscleGroup, muscleId, numWeeksBack],
    queryFn: () => getNormalizedStrengthAnalysis(muscleGroup, muscleId, numWeeksBack),
    enabled: numWeeksBack > 0 && (muscleGroup !== "ARMS" || muscleId !== undefined),
  });
};
