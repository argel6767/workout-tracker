import { useMemo } from "react";
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
import { useAnalyticsFilters } from "../hooks/useAnalyticsFilters";
import { LookbackStepper, MuscleTargetFilters } from "./analytics-filters";
import { formatAnalyticsLabel } from "../lib/analytics-formatters";
import { AiAnalysisCard } from "./card";

const formatNumber = (value: number) =>
  new Intl.NumberFormat("en-US", { maximumFractionDigits: 1 }).format(value);

export const WeeklyVolumeAnalytics = () => {
  const { data: muscles = [], isLoading: musclesLoading } = useGetMuscles();
  const {
    muscleGroup,
    muscleId,
    lookback: numWeeksBack,
    filteredMuscles,
    changeMuscleGroup,
    setMuscleId,
    decreaseLookback,
    increaseLookback,
  } = useAnalyticsFilters(muscles);
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
        <MuscleTargetFilters
          muscleGroup={muscleGroup}
          muscleId={muscleId}
          filteredMuscles={filteredMuscles}
          musclesLoading={musclesLoading}
          changeMuscleGroup={changeMuscleGroup}
          setMuscleId={setMuscleId}
          musclePlaceholder={`All ${formatAnalyticsLabel(muscleGroup)}`}
        />
        <LookbackStepper
          label="Weeks Back"
          value={numWeeksBack}
          onDecrease={decreaseLookback}
          onIncrease={increaseLookback}
        />
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
