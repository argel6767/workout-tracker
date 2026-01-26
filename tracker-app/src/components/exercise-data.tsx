import { useMemo, useState } from "react";
import { useGetExercises } from "../hooks/useGetExercises";
import type { MuscleGroup } from "../lib/form-dtos";

type ExerciseDataProps = {
  handleExerciseChange: (e: React.ChangeEvent<HTMLSelectElement>) => void;
}

export const ExerciseData = ({handleExerciseChange}: ExerciseDataProps) => {
  const [muscleGroup, setMuscleGroup] = useState<MuscleGroup>("CHEST");
  const { data = [], isLoading, isError } = useGetExercises()
  
  const exercises = useMemo(() => {
    return data.filter(
      (exercise) => exercise.primaryMuscleGroup === muscleGroup,
    );
  }, [data, muscleGroup]);
  
  const handleMuscleGroupChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setMuscleGroup(e.target.value as MuscleGroup);
  };
  
  if (isLoading)
    return <span className="loading loading-dots loading-xl"></span>;
  if (isError) return <div>Error fetching exercise data</div>;
  
  return (
    <main className="w-full flex flex-col gap-4">
      <label className="input">
        <span className="label">Muscle Group</span>
        <select
          value={muscleGroup}
          onChange={handleMuscleGroupChange}
          className="bg-base-200 rounded-lg p-1"
        >
          <option value="CHEST">Chest</option>
          <option value="BACK">Back</option>
          <option value="SHOULDERS">Shoulders</option>
          <option value="LEGS">Legs</option>
          <option value="ARMS">Arms</option>
          <option value="CORE">Core</option>
        </select>
      </label>
      <label className="input">
        <span className="label">Exercise</span>
        <select
          onChange={handleExerciseChange}
          className="bg-base-200 rounded-lg p-1"
        >
          {exercises!.map((exercise) => (
            <option key={exercise.id} value={exercise.id}>
              {exercise.name}
            </option>
          ))}
        </select>
      </label>
    </main>
  )
}