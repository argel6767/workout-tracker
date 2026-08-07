import { useMemo, useState } from "react";
import type { MuscleDto, MuscleGroup } from "../lib/form-dtos";

export const useAnalyticsFilters = (
  muscles: MuscleDto[],
  initialLookback = 5,
) => {
  const [muscleGroup, setMuscleGroup] = useState<MuscleGroup>("CHEST");
  const [muscleId, setMuscleId] = useState<number | undefined>();
  const [lookback, setLookback] = useState(initialLookback);

  const filteredMuscles = useMemo(
    () => muscles.filter((muscle) => muscle.muscleGroup === muscleGroup),
    [muscles, muscleGroup],
  );

  const changeMuscleGroup = (nextMuscleGroup: MuscleGroup) => {
    setMuscleGroup(nextMuscleGroup);
    setMuscleId(undefined);
  };

  const decreaseLookback = () => setLookback((current) => Math.max(1, current - 1));
  const increaseLookback = () => setLookback((current) => current + 1);

  return {
    muscleGroup,
    muscleId,
    lookback,
    filteredMuscles,
    changeMuscleGroup,
    setMuscleId,
    decreaseLookback,
    increaseLookback,
  };
};
