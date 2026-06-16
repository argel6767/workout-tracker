export type ExerciseType = "BODYWEIGHT" | "MACHINE" | "CABLE" | "FREE_WEIGHT";
export type Optional<T> = T | null;

export type NewExerciseDto = {
  name: string;
  description: string;
  musclesWorked: number[];
  exerciseType: ExerciseType;
};

export type MuscleGroup = "CHEST" | "BACK" | "LEGS" | "ARMS" | "CORE" | "SHOULDERS";

export type NewMuscleDto = {
  name: string;
  muscleGroup: MuscleGroup;
};

export type NewSetDto = {
  weight: number;
  reps: number;
};

export type NewWorkoutDto = {
  exerciseId: number;
  sets: NewSetDto[];
  workoutDate: Optional<string>;
};

export type MuscleDto = {
  id: number;
  name: string;
  muscleGroup: MuscleGroup;
};

export type ExerciseDto = {
  id: number;
  name: string;
  description: string;
  musclesWorked: MuscleDto[];
  primaryMuscleGroup: string;
};

type SetDto = {
  id: number;
  weight: number;
  reps: number;
};

export type WorkoutDto = {
  id: number;
  exercise: ExerciseDto;
  sets: SetDto[];
  workoutDate: string;
};

export type NewWeightDto = {
  weight: number;
  entryDate: Optional<string>;
};

export type WeightDto = {
  id: number;
  weight: number;
  entryDate: string;
};
