export type NewExerciseDto = {
  name: string;
  description: string;
  musclesWorked: number[];
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
};

export type WeightDto = {
  id: number;
  weight: number;
  entryDate: string;
};
