import type { MuscleGroup } from "./form-dtos";

export type DataPoint<K, V> = {
  key: K;
  value: V;
};

export type AnalyticsDto = {
  oneRepMaxes: DataPoint<string, number>[];
  avgWeightPerReps: DataPoint<string, number>[];
  totalVolumes: DataPoint<string, number>[];
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