import { useQuery } from '@tanstack/react-query'
import { getSetsByMuscleGroup } from '../api/analytics'

export const useGetSetsByMuscleGroup = () => {
  return useQuery({
    queryKey: ['setsByMuscleGroup'],
    queryFn: getSetsByMuscleGroup,
  })
}