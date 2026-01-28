import { useQuery } from '@tanstack/react-query';
import { getAiAnalysisByExercise } from '../api/analytics';

export const useGetAiAnalysisByExercise = (exerciseId: number) => {
  return useQuery({
    queryKey: ['aiAnalysis', exerciseId],
    queryFn: async() => await getAiAnalysisByExercise(exerciseId)
  })
}