import { useMemo, useState } from "react";
import {
  Bar,
  CartesianGrid,
  ComposedChart,
  Legend,
  Line,
  ReferenceLine,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { useGetMuscles } from "../hooks/useGetMuscles";
import { useGetWeeklyVolumeAnalysis } from "../hooks/useGetWeeklyVolumeAnalysis";
import { useGetWeeklyVolumeAiAnalysis } from "../hooks/useGetWeeklyVolumeAiAnalysis";
import type { MuscleGroup } from "../lib/form-dtos";
import { AiAnalysisCard } from "./card";

const MUSCLE_GROUPS: MuscleGroup[] = ["CHEST", "BACK", "SHOULDERS", "LEGS", "ARMS", "CORE"];

const formatLabel = (value: string) =>
  value.toLowerCase().replace(/(^|[-_])\w/g, (letter) => letter.toUpperCase()).replaceAll("_", " ");

const formatNumber = (value: number) =>
  new Intl.NumberFormat("en-US", { maximumFractionDigits: 1 }).format(value);

export const WeeklyVolumeAnalytics = () => {
  const [muscleGroup, setMuscleGroup] = useState<MuscleGroup>("CHEST");
  const [muscleId, setMuscleId] = useState<number | undefined>();
  const [numWeeksBack, setNumWeeksBack] = useState(5);
  const { data: muscles = [], isLoading: musclesLoading } = useGetMuscles();
  const { data, isLoading, isError } = useGetWeeklyVolumeAnalysis(
    muscleGroup,
    muscleId,
    numWeeksBack,
  );
  const {
    data: aiAnalysis,
    isLoading: aiLoading,
    isError: aiError,
  } = useGetWeeklyVolumeAiAnalysis(muscleGroup, muscleId, numWeeksBack);

  const filteredMuscles = useMemo(
    () => muscles.filter((muscle) => muscle.muscleGroup === muscleGroup),
    [muscles, muscleGroup],
  );

  const chartData = useMemo(() => {
    if (!data?.weeklyChanges.length) return [];
    const firstWeek = data.weeklyChanges[0].previousWeek;
    return [
      { week: firstWeek.startDate, totalVolume: firstWeek.totalVolume, volumeChange: null },
      ...data.weeklyChanges.map((change) => ({
        week: change.currentWeek.startDate,
        totalVolume: change.currentWeek.totalVolume,
        volumeChange: change.volumeChange,
      })),
    ];
  }, [data]);

  const latestChange = data?.weeklyChanges.at(-1);

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

        <label className="input">
          <span className="label">Specific Muscle</span>
          <select
            className="bg-base-200 rounded-lg p-1"
            value={muscleId ?? ""}
            disabled={musclesLoading}
            onChange={(event) => setMuscleId(event.target.value ? Number(event.target.value) : undefined)}
          >
            <option value="">All {formatLabel(muscleGroup)}</option>
            {filteredMuscles.map((muscle) => (
              <option key={muscle.id} value={muscle.id}>{muscle.name}</option>
            ))}
          </select>
        </label>

        <label className="flex flex-col gap-2 text-lg">
          Weeks Back
          <span className="flex gap-2 items-center">
            <button className="btn btn-square" disabled={numWeeksBack <= 1}
              onClick={() => setNumWeeksBack((weeks) => weeks - 1)}>-</button>
            <span className="min-w-6 text-center">{numWeeksBack}</span>
            <button className="btn btn-square"
              onClick={() => setNumWeeksBack((weeks) => weeks + 1)}>+</button>
          </span>
        </label>
      </div>

      {isLoading && <span className="loading loading-dots loading-xl self-center" />}
      {isError && <div className="text-center">Error loading weekly volume analytics</div>}
      {data && chartData.length > 0 && (
        <>
          <h2 className="text-2xl text-center">Weekly Volume — {data.targetName}</h2>
          <ResponsiveContainer width="100%" height={550}>
            <ComposedChart data={chartData} margin={{ top: 20, right: 30, left: 30, bottom: 10 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="week" />
              <YAxis yAxisId="volume" label={{ value: "Total Volume (lbs)", angle: -90, position: "insideLeft" }} />
              <YAxis yAxisId="change" orientation="right"
                label={{ value: "W/W Change (lbs)", angle: 90, position: "insideRight" }} />
              <Tooltip />
              <Legend />
              <ReferenceLine yAxisId="change" y={0} stroke="#888" />
              <Bar yAxisId="volume" dataKey="totalVolume" name="Total Volume" fill="#8884d8" />
              <Line yAxisId="change" type="monotone" dataKey="volumeChange" name="W/W Change"
                stroke="#00c49f" strokeWidth={4} connectNulls />
            </ComposedChart>
          </ResponsiveContainer>

          {latestChange && (
            <div className="stats stats-vertical md:stats-horizontal shadow">
              <div className="stat">
                <div className="stat-title">Latest Weekly Volume</div>
                <div className="stat-value text-primary">{formatNumber(latestChange.currentWeek.totalVolume)}</div>
                <div className="stat-desc">lbs across {latestChange.currentWeek.workouts.length} workouts</div>
              </div>
              <div className="stat">
                <div className="stat-title">Latest W/W Change</div>
                <div className="stat-value text-secondary">{latestChange.volumeChange >= 0 ? "+" : ""}{formatNumber(latestChange.volumeChange)}</div>
                <div className="stat-desc">
                  {latestChange.percentageChange === null
                    ? "No previous-week baseline"
                    : `${latestChange.percentageChange >= 0 ? "+" : ""}${latestChange.percentageChange.toFixed(1)}%`}
                </div>
              </div>
            </div>
          )}

          <AiAnalysisCard analysis={aiAnalysis} isLoading={aiLoading} isError={aiError}
            title="Weekly Volume Analysis" />
        </>
      )}
      {data && chartData.length === 0 && <div className="text-center">No weekly comparison data available</div>}
    </main>
  );
};
