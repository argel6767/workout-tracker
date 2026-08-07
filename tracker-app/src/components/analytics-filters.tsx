import type { MuscleDto, MuscleGroup } from "../lib/form-dtos";
import { formatAnalyticsLabel } from "../lib/analytics-formatters";

const MUSCLE_GROUPS: MuscleGroup[] = [
  "CHEST",
  "BACK",
  "SHOULDERS",
  "LEGS",
  "ARMS",
  "CORE",
];

type MuscleTargetFiltersProps = {
  muscleGroup: MuscleGroup;
  muscleId: number | undefined;
  filteredMuscles: MuscleDto[];
  musclesLoading: boolean;
  changeMuscleGroup: (muscleGroup: MuscleGroup) => void;
  setMuscleId: (muscleId: number | undefined) => void;
  muscleLabel?: string;
  musclePlaceholder: string;
  showMuscle?: boolean;
};

export const MuscleTargetFilters = ({
  muscleGroup,
  muscleId,
  filteredMuscles,
  musclesLoading,
  changeMuscleGroup,
  setMuscleId,
  muscleLabel = "Specific Muscle",
  musclePlaceholder,
  showMuscle = true,
}: MuscleTargetFiltersProps) => (
  <>
    <label className="input">
      <span className="label">Muscle Group</span>
      <select
        className="bg-base-200 rounded-lg p-1"
        value={muscleGroup}
        onChange={(event) => changeMuscleGroup(event.target.value as MuscleGroup)}
      >
        {MUSCLE_GROUPS.map((group) => (
          <option key={group} value={group}>{formatAnalyticsLabel(group)}</option>
        ))}
      </select>
    </label>

    {showMuscle && <label className="input">
      <span className="label">{muscleLabel}</span>
      <select
        className="bg-base-200 rounded-lg p-1"
        value={muscleId ?? ""}
        disabled={musclesLoading}
        onChange={(event) => setMuscleId(
          event.target.value ? Number(event.target.value) : undefined,
        )}
      >
        <option value="">{musclePlaceholder}</option>
        {filteredMuscles.map((muscle) => (
          <option key={muscle.id} value={muscle.id}>{muscle.name}</option>
        ))}
      </select>
    </label>}
  </>
);

type LookbackStepperProps = {
  label: string;
  value: number;
  onDecrease: () => void;
  onIncrease: () => void;
};

export const LookbackStepper = ({
  label,
  value,
  onDecrease,
  onIncrease,
}: LookbackStepperProps) => {
  const accessibleLabel = label.toLowerCase();

  return (
    <label className="flex flex-col gap-2 text-lg">
      {label}
      <span className="flex gap-2 items-center">
        <button
          type="button"
          className="btn btn-square"
          disabled={value <= 1}
          aria-label={`Decrease ${accessibleLabel}`}
          onClick={onDecrease}
        >-</button>
        <span className="min-w-6 text-center">{value}</span>
        <button
          type="button"
          className="btn btn-square"
          aria-label={`Increase ${accessibleLabel}`}
          onClick={onIncrease}
        >+</button>
      </span>
    </label>
  );
};
