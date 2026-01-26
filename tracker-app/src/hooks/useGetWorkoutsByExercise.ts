import { useQuery } from '@tanstack/react-query';
import { getWorkoutsByExercise } from '../api/workouts';

export const useGetWorkoutsByExercise = (exerciseId: number) => {
  return useQuery({
    queryKey: ['workouts', exerciseId],
    queryFn: async() => await getWorkoutsByExercise(exerciseId)
  })
}