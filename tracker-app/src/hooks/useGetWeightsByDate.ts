import { useQuery } from "@tanstack/react-query";
import { getWeightsByDate } from "../api/weights";

export const useGetWeightsByDate = (numMonthsBack: number) => {
  return useQuery({
    queryKey: ["weights", numMonthsBack],
    queryFn: async() => await getWeightsByDate(numMonthsBack),
  });
}