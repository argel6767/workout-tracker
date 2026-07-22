import { useGetStrongestExercises } from "../hooks/useGetStrongestExercises";
import { useGetStrongestExercisesAiAnalysis } from "../hooks/useGetStrongestExercisesAiAnalysis";
import { AiAnalysisCard } from "./card";

const formatNumber = (value: number) =>
  new Intl.NumberFormat("en-US", { maximumFractionDigits: 1 }).format(value);

export const StrongestExercisesTables = () => {
  const { data, isLoading, isError } = useGetStrongestExercises();
  const {
    data: aiAnalysis,
    isLoading: aiLoading,
    isError: aiError,
  } = useGetStrongestExercisesAiAnalysis();

  if (isLoading) return <span className="loading loading-dots loading-xl" />;
  if (isError) return <div>Error loading strongest exercises</div>;
  if (!data) return <div>No strongest-exercise data available</div>;

  return (
    <main className="w-full flex flex-col gap-10">
      <AiAnalysisCard
        analysis={aiAnalysis}
        isLoading={aiLoading}
        isError={aiError}
        title="Strongest Exercises Analysis"
      />
      <section>
        <h2 className="text-2xl font-semibold text-center mb-4">Strongest Exercise by Muscle Group</h2>
        <div className="overflow-x-auto">
          <table className="table table-zebra">
            <thead>
              <tr>
                <th>Muscle Group</th>
                <th>Exercise</th>
                <th className="text-right">Estimated 1RM</th>
              </tr>
            </thead>
            <tbody>
              {data.muscleGroups.map((row) => (
                <tr key={row.muscleGroup}>
                  <td className="font-medium">{row.muscleGroup}</td>
                  <td>{row.exerciseName}</td>
                  <td className="text-right">{formatNumber(row.oneRepMax)} lbs</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section>
        <h2 className="text-2xl font-semibold text-center mb-4">Strongest Exercise by Muscle</h2>
        <div className="overflow-x-auto">
          <table className="table table-zebra">
            <thead>
              <tr>
                <th>Muscle</th>
                <th>Exercise</th>
                <th className="text-right">Estimated 1RM</th>
                <th className="text-right">Average Weight per Rep</th>
              </tr>
            </thead>
            <tbody>
              {data.muscles.map((row) => (
                <tr key={row.muscleId}>
                  <td className="font-medium">{row.muscleName}</td>
                  <td>{row.exerciseName}</td>
                  <td className="text-right">{formatNumber(row.oneRepMax)} lbs</td>
                  <td className="text-right">{formatNumber(row.avgWeightPerRep)} lbs</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
};
