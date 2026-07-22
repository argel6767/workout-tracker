import { useQuery } from "@tanstack/react-query";
import { getSetsBreakdownAiAnalysis } from "../api/analytics";

export const useGetSetsBreakdownAiAnalysis = () => useQuery({
  queryKey: ["setsBreakdownAiAnalysis"],
  queryFn: getSetsBreakdownAiAnalysis,
});
