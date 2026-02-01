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
import { ExerciseData } from "./exercise-data";

type AnalyticType = "general" | "relativeStrength" | "weight";

export const AnalyticsContainer = () => {
  const [exerciseId, setExerciseId] = useState<number>(1);
  const [numMonthsBack, setNumberMonthsBack] = useState<number>(2);
  const [analyticType, setAnalyticType] = useState<AnalyticType>("general");

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
        <ExerciseData handleExerciseChange={handleExerciseChange} />
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
          </select>
        </label>

        <div>
          <label className="flex flex-col gap-2 text-lg">
            Months Back
            <span className="flex gap-2 text-lg items-center">
              <button
                className="btn btn-square"
                onClick={decreaseMonthsBack}
                disabled={numMonthsBack <= 1}
              >
                -
              </button>
              <span>{numMonthsBack}</span>
              <button className="btn btn-square" onClick={increaseMonthsBack}>
                +
              </button>
            </span>
          </label>
        </div>
      </div>
      {analyticType === "general" && (
        <ExerciseAnalytics
          exerciseId={exerciseId}
          numMonthsBack={numMonthsBack}
        />
      )}
      {analyticType === "relativeStrength" && (
        <RelativeStrengthAnalytics
          exerciseId={exerciseId}
          numMonthsBack={numMonthsBack}
        />
      )}
      {analyticType === "weight" && (
        <WeightAnalytics numMonthsBack={numMonthsBack} />
      )}
    </main>
  );
};

type AnalyticsProps = {
  exerciseId: number;
  numMonthsBack: number;
};

type DataPointType = "orm" | "avg" | "vol";

const ExerciseAnalytics = ({ exerciseId, numMonthsBack }: AnalyticsProps) => {
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
    return <div>No data available</div>;
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
        xAxisDataKey="date"
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

const RelativeStrengthAnalytics = ({
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
    return <div>No data available</div>;
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
        <XAxis dataKey={xAxisDataKey}></XAxis>
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
