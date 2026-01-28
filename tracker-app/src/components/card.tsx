import { useGetAiAnalysisByExercise } from "../hooks/useGetAiAnalysisByExercise";

type AiAnalysisCardProps = {
  exerciseId: number;
}

export const AiAnalysisCard = ({ exerciseId }: AiAnalysisCardProps) => {
  const { data, isLoading, isError } = useGetAiAnalysisByExercise(exerciseId);
  
  if (isLoading)
    return <span className="loading loading-dots loading-xl"></span>;
  if (isError) return <div>Error fetching AI analysis</div>;
  
  if (!data)
    return <div>No AI analysis available for given exercise</div>;
  
  return (
    <div className="card card-border bg-base-100">
      <div className="card-body">
        <h2 className="card-title">Progress Analysis</h2>
        <p>{data.body}</p>
      </div>
    </div>
  )
}
