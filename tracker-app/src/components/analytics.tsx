import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Label } from "recharts";
import {useState} from "react";
import { useGetRelativeStrengthByExercise } from "../hooks/useGetGetRelativeStrengthByExercise";
import { useGetExerciseAnalytics } from "../hooks/useGetExerciseAnalytics";
import { ExerciseData } from "./exercise-data";

export const AnalyticsContainer = () => {
  const [exerciseId, setExerciseId] = useState<number>(1);
  const [numMonthsBack, setNumberMonthsBack] = useState<number>(2);
  
  const increaseMonthsBack = () => {
    setNumberMonthsBack(numMonthsBack + 1);
  };
  
  const decreaseMonthsBack = () => {
    setNumberMonthsBack(numMonthsBack - 1);
  };
  
  const handleExerciseChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setExerciseId(parseInt(event.target.value));
  };
  
  return (
    <main className="p-2 flex flex-col gap-4">
      <div className="pl-4">
        <ExerciseData handleExerciseChange={handleExerciseChange}/>
      </div>
      <ExerciseAnalytics exerciseId={exerciseId} numMonthsBack={numMonthsBack}/>
    </main>
  )
}

type ExerciseAnalyticsProps = {
  exerciseId: number;
  numMonthsBack: number;
}

type DataPointType = "orm" | "avg" | "vol";

const ExerciseAnalytics = ({exerciseId, numMonthsBack}: ExerciseAnalyticsProps) => {
  const { data: exerciseAnalytics, isLoading: isAnalyticsLoading, isError: isAnalyticsError } = useGetExerciseAnalytics(exerciseId, numMonthsBack);
  const [selectedDataPointType, setSelectedDataPointType] = useState<DataPointType>("orm");
  
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
      <LineGraph data={dataPoints} lineDataKey="value" xAxisDataKey="date" yAxisLabel="Weight"/>
      <span className="flex justify-center gap-4 py-4">
      <button className="btn btn-neutral" onClick={() => setSelectedDataPointType("orm")}>One Rep Max</button>
      <button className="btn btn-neutral" onClick={() => setSelectedDataPointType("avg")}>Average Weight Per Rep</button>
      <button className="btn btn-neutral" onClick={() => setSelectedDataPointType("vol")}>Total Weight Volume Pushed</button>
      </span>
    </main>
  );
};

type GraphProps<T extends Record<string, unknown>> = {
  data: T[];
  xAxisDataKey: string;
  yAxisLabel: string;
  lineDataKey: string;
};
const LineGraph = <T extends Record<string, unknown>> ({data, lineDataKey, xAxisDataKey, yAxisLabel}: GraphProps<T>) => {
  return (
    <ResponsiveContainer width={"100%"} height={600} className={"py-2"}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey={xAxisDataKey} >
        </XAxis>
        <YAxis>
          <Label value={yAxisLabel} angle={-90} position="insideLeft" />
        </YAxis>
        <Tooltip />
        <Line type="monotone" dataKey={lineDataKey} stroke="#8884d8" strokeWidth={5}/>
      </LineChart>
   </ResponsiveContainer>
  );
};
