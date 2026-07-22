import { useGetAiAnalysisByExercise } from "../hooks/useGetAiAnalysisByExercise";
import type { ChatResponseDto } from "../lib/analytics-dtos";

type AiAnalysisCardProps = {
  analysis: ChatResponseDto | undefined;
  isLoading: boolean;
  isError: boolean;
  title?: string;
  emptyMessage?: string;
}

export const AiAnalysisCard = ({
  analysis,
  isLoading,
  isError,
  title = "Progress Analysis",
  emptyMessage = "No AI analysis available",
}: AiAnalysisCardProps) => {
  if (isLoading)
    return <span className="loading loading-dots loading-xl"></span>;
  if (isError) return <div>Error fetching AI analysis</div>;
  if (!analysis) return <div>{emptyMessage}</div>;
  
  return (
    <div className="card card-border bg-base-100">
      <div className="card-body">
        <h2 className="card-title">{title}</h2>
        <p>{analysis.body}</p>
      </div>
    </div>
  )
}

export const ExerciseAiAnalysisCard = ({ exerciseId }: { exerciseId: number }) => {
  const { data, isLoading, isError } = useGetAiAnalysisByExercise(exerciseId);
  return (
    <AiAnalysisCard
      analysis={data}
      isLoading={isLoading}
      isError={isError}
      emptyMessage="An exercise needs to be selected for AI analysis"
    />
  );
};
