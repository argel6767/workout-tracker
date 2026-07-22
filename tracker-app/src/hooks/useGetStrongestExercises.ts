import { useQuery } from "@tanstack/react-query";
import { getStrongestExercises } from "../api/analytics";

export const useGetStrongestExercises = () => useQuery({
  queryKey: ["strongestExercises"],
  queryFn: getStrongestExercises,
});
