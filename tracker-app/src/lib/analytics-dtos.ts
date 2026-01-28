import type { MuscleGroup } from "./form-dtos";

type DataPoint = {
  date: string;
  value: number;
};

export type AnalyticsDto = {
  oneRepMaxes: DataPoint[];
  avgWeightPerReps: DataPoint[];
  totalVolumes: DataPoint[];
};

export type RelativeStrengthDto = {
  weight: number;
  oneRepMax: number;
  relativeStrength: number;
  entryDate: string;
};

export type StrongestExerciseByMuscleDto = {
  exerciseId: number;
  exerciseName: string;
  oneRepMax: number;
  avgWeightPerRep: number;
};

export type StrongestExerciseByMuscleGroupDto = {
  exerciseId: number;
  exerciseName: string;
  oneRepMax: number;
  muscleGroup: MuscleGroup;
};

export type ChatResponseDto = {
  body: string;
  timestamp: string;
}