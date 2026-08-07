import { useState } from "react";
import { ExerciseData } from "./exercise-data";
import { ExerciseAnalytics, RelativeStrengthAnalytics, WeightAnalytics } from "./linegraphs";
import { SetsByMuscleGroupPieChart, WorkoutBreakdownPieChart } from "./piecharts";
import { WeeklyVolumeAnalytics } from "./weekly-volume";
import { WeeklyOneRepMaxAnalytics } from "./weekly-one-rep-max";
import { NormalizedStrengthAnalytics } from "./normalized-strength";

type AnalyticType = "general" | "relativeStrength" | "weight" | "workoutBreakdown" | "setBreakdown" | "weeklyVolume" | "weeklyOneRepMax" | "normalizedStrength";

export const AnalyticsContainer = () => {
  const [exerciseId, setExerciseId] = useState<number>(-1);
  const [numMonthsBack, setNumberMonthsBack] = useState<number>(2);
  const [analyticType, setAnalyticType] = useState<AnalyticType>("general");
  const hasDedicatedFilters = analyticType === "weeklyVolume" || analyticType === "weeklyOneRepMax" || analyticType === "normalizedStrength";

  const increaseMonthsBack = () => {
    setNumberMonthsBack(numMonthsBack + 1);
  };

  const decreaseMonthsBack = () => {
    setNumberMonthsBack(numMonthsBack - 1);
  };

  const handleExerciseChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
  ) => {
    setExerciseId(parseInt(event.target.value));
  };

  const handleAnalyticTypeChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
  ) => {
    setAnalyticType(event.target.value as AnalyticType);
  };

  return (
    <main className="p-2 flex flex-col gap-4">
      <div className="px-4 flex justify-between gap-4 items-center">
        {!hasDedicatedFilters && <ExerciseData handleExerciseChange={handleExerciseChange} />}
        <label className="input">
          <span className="label-text">Analytic Type</span>
          <select
            value={analyticType}
            onChange={handleAnalyticTypeChange}
            className="bg-base-200 rounded-lg"
          >
            <option value="general">General</option>
            <option value="relativeStrength">Relative Strength</option>
            <option value="weight">Weight</option>
            <option value="workoutBreakdown">Workout Breakdown</option>
            <option value="setBreakdown">Set Breakdown</option>
            <option value="weeklyVolume">Weekly Volume</option>
            <option value="weeklyOneRepMax">Weekly One Rep Max</option>
            <option value="normalizedStrength">Normalized Strength</option>
          </select>
        </label>

        {!hasDedicatedFilters && <div>
          <label className="flex flex-col gap-2 text-lg">
            Months Back
            <span className="flex gap-2 text-lg items-center">
              <button
                className="btn btn-square"
                onClick={decreaseMonthsBack}
                disabled={numMonthsBack <= 1}
                aria-label="Decrease months back"
              >
                -
              </button>
              <span>{numMonthsBack}</span>
              <button className="btn btn-square" onClick={increaseMonthsBack} aria-label="Increase months back">
                +
              </button>
            </span>
          </label>
        </div>}
      </div>
      <div>
      <AnalyticDisplay exerciseId={exerciseId} numMonthsBack={numMonthsBack} analyticType={analyticType} />
      </div>
    </main>
  );
};

type AnalyticDisplayProps = {
  exerciseId: number;
  numMonthsBack: number;
  analyticType: AnalyticType
}

const AnalyticDisplay = ({ exerciseId, numMonthsBack, analyticType }: AnalyticDisplayProps) => {
  if (analyticType === "general") {
    return (
      <ExerciseAnalytics
        exerciseId={exerciseId}
        numMonthsBack={numMonthsBack}
      />
    );
  }
  if (analyticType === "relativeStrength") {
    return (
      <RelativeStrengthAnalytics
        exerciseId={exerciseId}
        numMonthsBack={numMonthsBack}
      />
    );
  }
  if (analyticType === "weight") {
    return (
      <WeightAnalytics numMonthsBack={numMonthsBack} />
    );
  }
  if (analyticType === "workoutBreakdown") {
    return (
      <WorkoutBreakdownPieChart />
    );
  }
  if (analyticType === "setBreakdown") {
    return (
      <SetsByMuscleGroupPieChart />
    );
  }
  if (analyticType === "weeklyVolume") {
    return <WeeklyVolumeAnalytics />;
  }
  if (analyticType === "weeklyOneRepMax") {
    return <WeeklyOneRepMaxAnalytics />;
  }
  if (analyticType === "normalizedStrength") {
    return <NormalizedStrengthAnalytics />;
  }
}
