import { useMemo, useState } from "react";
import { CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { useGetMuscles } from "../hooks/useGetMuscles";
import { useGetWeeklyOneRepMaxAnalysis } from "../hooks/useGetWeeklyOneRepMaxAnalysis";
import { useGetWeeklyOneRepMaxAiAnalysis } from "../hooks/useGetWeeklyOneRepMaxAiAnalysis";
import type { MuscleGroup } from "../lib/form-dtos";
import { AiAnalysisCard } from "./card";

const MUSCLE_GROUPS: MuscleGroup[] = ["CHEST", "BACK", "SHOULDERS", "LEGS", "ARMS", "CORE"];
const COLORS = ["#8884d8", "#00c49f", "#ffbb28", "#ff8042", "#0088fe", "#ff7c7c", "#82ca9d"];
const formatLabel = (value: string) =>
  value.toLowerCase().replace(/(^|[-_])\w/g, (letter) => letter.toUpperCase()).replaceAll("_", " ");
const formatNumber = (value: number) =>
  new Intl.NumberFormat("en-US", { maximumFractionDigits: 1 }).format(value);

export const WeeklyOneRepMaxAnalytics = () => {
  const [muscleGroup, setMuscleGroup] = useState<MuscleGroup>("CHEST");
  const [muscleId, setMuscleId] = useState<number | undefined>();
  const [numWeeksBack, setNumWeeksBack] = useState(5);
  const { data: muscles = [], isLoading: musclesLoading } = useGetMuscles();
  const { data, isLoading, isError } = useGetWeeklyOneRepMaxAnalysis(muscleId, numWeeksBack);
  const { data: aiAnalysis, isLoading: aiLoading, isError: aiError } =
    useGetWeeklyOneRepMaxAiAnalysis(muscleId, numWeeksBack);

  const filteredMuscles = useMemo(
    () => muscles.filter((muscle) => muscle.muscleGroup === muscleGroup),
    [muscles, muscleGroup],
  );

  const chartData = useMemo(() => {
    const weeks = new Map<string, Record<string, string | number | null>>();
    data?.exercises.forEach((exercise) => {
      const dataKey = `exercise-${exercise.exerciseId}`;
      const firstPrevious = exercise.weeklyChanges[0]?.previousWeek;
      if (firstPrevious) {
        const row = weeks.get(firstPrevious.startDate) ?? { week: firstPrevious.startDate };
        row[dataKey] = firstPrevious.oneRepMax;
        weeks.set(firstPrevious.startDate, row);
      }
      exercise.weeklyChanges.forEach((change) => {
        const row = weeks.get(change.currentWeek.startDate) ?? { week: change.currentWeek.startDate };
        row[dataKey] = change.currentWeek.oneRepMax;
        weeks.set(change.currentWeek.startDate, row);
      });
    });
    return [...weeks.values()].sort((first, second) => String(first.week).localeCompare(String(second.week)));
  }, [data]);

  return (
    <main className="flex flex-col gap-6">
      <div className="flex flex-wrap justify-center gap-4">
        <label className="input">
          <span className="label">Muscle Group</span>
          <select className="bg-base-200 rounded-lg p-1" value={muscleGroup}
            onChange={(event) => {
              setMuscleGroup(event.target.value as MuscleGroup);
              setMuscleId(undefined);
            }}>
            {MUSCLE_GROUPS.map((group) => <option key={group} value={group}>{formatLabel(group)}</option>)}
          </select>
        </label>
        <label className="input">
          <span className="label">Muscle</span>
          <select className="bg-base-200 rounded-lg p-1" value={muscleId ?? ""} disabled={musclesLoading}
            onChange={(event) => setMuscleId(event.target.value ? Number(event.target.value) : undefined)}>
            <option value="">Select a muscle</option>
            {filteredMuscles.map((muscle) => <option key={muscle.id} value={muscle.id}>{muscle.name}</option>)}
          </select>
        </label>
        <label className="flex flex-col gap-2 text-lg">
          Weeks Back
          <span className="flex gap-2 items-center">
            <button className="btn btn-square" disabled={numWeeksBack <= 1}
              onClick={() => setNumWeeksBack((weeks) => weeks - 1)}>-</button>
            <span className="min-w-6 text-center">{numWeeksBack}</span>
            <button className="btn btn-square" onClick={() => setNumWeeksBack((weeks) => weeks + 1)}>+</button>
          </span>
        </label>
      </div>

      {!muscleId && <div className="text-xl text-center">Select a muscle to compare estimated one-rep maxes</div>}
      {isLoading && <span className="loading loading-dots loading-xl self-center" />}
      {isError && <div className="text-center">Error loading weekly one-rep-max analytics</div>}
      {data && data.exercises.length === 0 && <div className="text-center">No workouts found for this muscle</div>}
      {data && data.exercises.length > 0 && (
        <>
          <h2 className="text-2xl text-center">Weekly Estimated 1RM — {data.muscleName}</h2>
          <p className="text-center text-base-content/70">
            Age {data.age} · Body weight {formatNumber(data.bodyWeight.weight)} lbs
            {data.bodyWeight.entryDate ? ` as of ${data.bodyWeight.entryDate}` : ""}
          </p>
          <ResponsiveContainer width="100%" height={550}>
            <LineChart data={chartData} margin={{ top: 20, right: 30, left: 30, bottom: 10 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="week" />
              <YAxis label={{ value: "Estimated 1RM (lbs)", angle: -90, position: "insideLeft" }} />
              <Tooltip />
              <Legend />
              {data.exercises.map((exercise, index) => (
                <Line key={exercise.exerciseId} type="monotone" dataKey={`exercise-${exercise.exerciseId}`}
                  name={exercise.exerciseName} stroke={COLORS[index % COLORS.length]} strokeWidth={4}
                  connectNulls={false} />
              ))}
            </LineChart>
          </ResponsiveContainer>

          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {data.exercises.map((exercise) => {
              const latest = exercise.weeklyChanges.at(-1);
              return (
                <div key={exercise.exerciseId} className="stat shadow">
                  <div className="stat-title">{exercise.exerciseName}</div>
                  <div className="stat-value text-primary text-3xl">
                    {latest?.currentWeek.oneRepMax == null ? "—" : formatNumber(latest.currentWeek.oneRepMax)}
                  </div>
                  <div className="stat-desc">
                    {latest?.oneRepMaxChange == null
                      ? "No W/W comparison"
                      : `${latest.oneRepMaxChange >= 0 ? "+" : ""}${formatNumber(latest.oneRepMaxChange)} lbs (${latest.percentageChange?.toFixed(1)}%)`}
                  </div>
                </div>
              );
            })}
          </div>

          <AiAnalysisCard analysis={aiAnalysis} isLoading={aiLoading} isError={aiError}
            title="Weekly One-Rep-Max Analysis" />
        </>
      )}
    </main>
  );
};
