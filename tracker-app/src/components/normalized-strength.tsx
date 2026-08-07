import { useMemo, useState } from "react";
import {
  CartesianGrid,
  Line,
  LineChart,
  ReferenceLine,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { useGetMuscles } from "../hooks/useGetMuscles";
import { useGetNormalizedStrengthAnalysis } from "../hooks/useGetNormalizedStrengthAnalysis";
import type { MuscleDto, MuscleGroup } from "../lib/form-dtos";

const MUSCLE_GROUPS: MuscleGroup[] = ["CHEST", "BACK", "SHOULDERS", "LEGS", "ARMS", "CORE"];

const formatLabel = (value: string) =>
  value.toLowerCase().replace(/(^|[-_])\w/g, (letter) => letter.toUpperCase()).replaceAll("_", " ");

const formatNumber = (value: number) =>
  new Intl.NumberFormat("en-US", { maximumFractionDigits: 1 }).format(value);

const formatBaselineDifference = (strengthIndex: number) => {
  const difference = strengthIndex - 100;
  if (difference === 0) return "At baseline";
  return `${formatNumber(Math.abs(difference))}% ${difference > 0 ? "above" : "below"} baseline`;
};

export const NormalizedStrengthAnalytics = () => {
  const [muscleGroup, setMuscleGroup] = useState<MuscleGroup>("CHEST");
  const [muscleId, setMuscleId] = useState<number | undefined>();
  const [numWeeksBack, setNumWeeksBack] = useState(5);
  const { data: muscles = [], isLoading: musclesLoading } = useGetMuscles();
  const { data, isLoading, isError } = useGetNormalizedStrengthAnalysis(
    muscleGroup,
    muscleId,
    numWeeksBack,
  );

  const filteredMuscles = useMemo(
    () => muscles.filter((muscle: MuscleDto) => muscle.muscleGroup === muscleGroup),
    [muscles, muscleGroup],
  );
  const requiresArmMuscle = muscleGroup === "ARMS" && muscleId === undefined;
  const latestPoint = data?.trend.at(-1);

  return (
    <main className="flex flex-col gap-6">
      <div className="flex flex-wrap justify-center gap-4">
        <label className="input">
          <span className="label">Muscle Group</span>
          <select
            className="bg-base-200 rounded-lg p-1"
            value={muscleGroup}
            onChange={(event) => {
              setMuscleGroup(event.target.value as MuscleGroup);
              setMuscleId(undefined);
            }}
          >
            {MUSCLE_GROUPS.map((group) => (
              <option key={group} value={group}>{formatLabel(group)}</option>
            ))}
          </select>
        </label>

        {muscleGroup === "ARMS" && (
          <label className="input">
            <span className="label">Specific Muscle</span>
            <select
              className="bg-base-200 rounded-lg p-1"
              value={muscleId ?? ""}
              disabled={musclesLoading}
              onChange={(event) => setMuscleId(event.target.value ? Number(event.target.value) : undefined)}
            >
              <option value="">Select biceps or triceps</option>
              {filteredMuscles.map((muscle: MuscleDto) => (
                <option key={muscle.id} value={muscle.id}>{muscle.name}</option>
              ))}
            </select>
          </label>
        )}

        <label className="flex flex-col gap-2 text-lg">
          Weeks Back
          <span className="flex gap-2 items-center">
            <button
              className="btn btn-square"
              disabled={numWeeksBack <= 1}
              aria-label="Decrease weeks back"
              onClick={() => setNumWeeksBack((weeks) => weeks - 1)}
            >-</button>
            <span className="min-w-6 text-center">{numWeeksBack}</span>
            <button
              className="btn btn-square"
              aria-label="Increase weeks back"
              onClick={() => setNumWeeksBack((weeks) => weeks + 1)}
            >+</button>
          </span>
        </label>
      </div>

      {requiresArmMuscle && (
        <div className="text-xl text-center">Select biceps or triceps to view normalized strength</div>
      )}
      {!requiresArmMuscle && isLoading && <span className="loading loading-dots loading-xl self-center" />}
      {!requiresArmMuscle && isError && (
        <div className="text-center">Error loading normalized strength analytics</div>
      )}
      {!requiresArmMuscle && data && data.trend.length === 0 && (
        <div className="text-center">No normalized strength data available</div>
      )}
      {!requiresArmMuscle && data && data.trend.length > 0 && latestPoint && (
        <>
          <h2 className="text-2xl text-center">Normalized Strength  {data.targetName}</h2>
          <p className="text-center text-base-content/70">
            Each exercise starts at 100; weekly values show progress relative to its first session.
          </p>
          <ResponsiveContainer width="100%" height={550}>
            <LineChart data={data.trend} margin={{ top: 20, right: 30, left: 30, bottom: 10 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="weekStart" />
              <YAxis label={{ value: "Strength Index", angle: -90, position: "insideLeft" }} />
              <Tooltip />
              <ReferenceLine y={100} stroke="#888" strokeDasharray="5 5" label="Baseline" />
              <Line
                type="monotone"
                dataKey="averageStrengthIndex"
                name="Normalized Strength"
                stroke="#8884d8"
                strokeWidth={4}
              />
            </LineChart>
          </ResponsiveContainer>

          <div className="stats stats-vertical md:stats-horizontal shadow">
            <div className="stat">
              <div className="stat-title">Latest Strength Index</div>
              <div className="stat-value text-primary">{formatNumber(latestPoint.averageStrengthIndex)}</div>
              <div className="stat-desc">{formatBaselineDifference(latestPoint.averageStrengthIndex)}</div>
            </div>
            <div className="stat">
              <div className="stat-title">Latest Coverage</div>
              <div className="stat-value text-secondary">{latestPoint.exerciseCount}</div>
              <div className="stat-desc">
                {latestPoint.exerciseCount} {latestPoint.exerciseCount === 1 ? "exercise" : "exercises"} represented
              </div>
            </div>
          </div>
        </>
      )}
    </main>
  );
};
