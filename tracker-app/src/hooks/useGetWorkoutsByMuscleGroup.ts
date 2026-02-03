import { useQuery } from '@tanstack/react-query';
import { getWorkoutsByMuscleGroup } from '../api/analytics';

export const useGetWorkoutsByMuscleGroup = () => {
  return useQuery({
    queryKey: ['workoutsByMuscleGroup'],
    queryFn: getWorkoutsByMuscleGroup,
  })
}