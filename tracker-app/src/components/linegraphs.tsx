import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Label,
} from "recharts";
import { useState } from "react";
import { useGetRelativeStrengthByExercise } from "../hooks/useGetGetRelativeStrengthByExercise";
import { useGetWeightsByDate } from "../hooks/useGetWeightsByDate";
import { useGetExerciseAnalytics } from "../hooks/useGetExerciseAnalytics";

type AnalyticsProps = {
  exerciseId: number;
  numMonthsBack: number;
};

type DataPointType = "orm" | "avg" | "vol";

export const ExerciseAnalytics = ({ exerciseId, numMonthsBack }: AnalyticsProps) => {
  const {
    data: exerciseAnalytics,
    isLoading: isAnalyticsLoading,
    isError: isAnalyticsError,
  } = useGetExerciseAnalytics(exerciseId, numMonthsBack);
  const [selectedDataPointType, setSelectedDataPointType] =
    useState<DataPointType>("orm");

  if (isAnalyticsLoading) {
    return <span className="loading loading-dots loading-xl"></span>;
  }

  if (isAnalyticsError) {
    return <div>Error loading analytics</div>;
  }

  if (!exerciseAnalytics) {
    return <h2 className="text-xl py-2 text-center">No data available. Pick an exercise</h2>;
  }

  const dataPointsMap = {
    orm: exerciseAnalytics.oneRepMaxes,
    avg: exerciseAnalytics.avgWeightPerReps,
    vol: exerciseAnalytics.totalVolumes,
  };

  const dataPoints = dataPointsMap[selectedDataPointType] || [];

  return (
    <main>
      <LineGraph
        data={dataPoints}
        lineDataKey="value"
        xAxisDataKey="key"
        yAxisLabel="Weight"
      />
      <span className="flex justify-center gap-4 py-4">
        <button
          className="btn btn-neutral"
          onClick={() => setSelectedDataPointType("orm")}
        >
          One Rep Max
        </button>
        <button
          className="btn btn-neutral"
          onClick={() => setSelectedDataPointType("avg")}
        >
          Average Weight Per Rep
        </button>
        <button
          className="btn btn-neutral"
          onClick={() => setSelectedDataPointType("vol")}
        >
          Total Weight Volume Pushed
        </button>
      </span>
    </main>
  );
};

type WeightAnalyticsProps = {
  numMonthsBack: number;
};

export const WeightAnalytics = ({ numMonthsBack }: WeightAnalyticsProps) => {
  const { data, isLoading, isError } = useGetWeightsByDate(numMonthsBack);

  if (isLoading) {
    return <span className="loading loading-dots loading-xl"></span>;
  }

  if (isError) {
    return <div>Error loading analytics</div>;
  }

  if (!data || data.length === 0) {
    return <div>No data available</div>;
  }

  return (
    <main>
      <LineGraph
        data={data}
        lineDataKey="weight"
        xAxisDataKey="entryDate"
        yAxisLabel="Weight (lbs)"
      />
    </main>
  );
};

export const RelativeStrengthAnalytics = ({
  exerciseId,
  numMonthsBack,
}: AnalyticsProps) => {
  const { data, isLoading, isError } = useGetRelativeStrengthByExercise(
    exerciseId,
    numMonthsBack,
  );

  if (isLoading) {
    return <span className="loading loading-dots loading-xl"></span>;
  }

  if (isError) {
    return <div>Error loading analytics</div>;
  }

  if (!data || data.length === 0) {
    return <h2 className="text-xl py-2 text-center">No data available. Pick an exercise</h2>;
  }

  return (
    <main>
      <LineGraph data={data} lineDataKey="relativeStrength" xAxisDataKey="entryDate" yAxisLabel="Relative Strength" />
    </main>
  );
};

type GraphProps<T extends Record<string, unknown>> = {
  data: T[];
  xAxisDataKey: string;
  yAxisLabel: string;
  lineDataKey: string;
};
const LineGraph = <T extends Record<string, unknown>>({
  data,
  lineDataKey,
  xAxisDataKey,
  yAxisLabel,
}: GraphProps<T>) => {
  return (
    <ResponsiveContainer width={"100%"} height={600} className={"py-2"}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey={xAxisDataKey} name="date"></XAxis>
        <YAxis>
          <Label value={yAxisLabel} angle={-90} position="insideLeft" />
        </YAxis>
        <Tooltip />
        <Line
          type="monotone"
          dataKey={lineDataKey}
          stroke="#8884d8"
          strokeWidth={5}
        />
      </LineChart>
    </ResponsiveContainer>
  );
};
