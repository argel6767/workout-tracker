import type { MuscleGroup, WeightDto, WorkoutDto } from "./form-dtos";

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

export type WeeklyVolumeDto = {
  startDate: string;
  endDate: string;
  workouts: WorkoutDto[];
  totalVolume: number;
};

export type StrongestExerciseForMuscleDto = {
  muscleId: number;
  muscleName: string;
  exerciseId: number;
  exerciseName: string;
  oneRepMax: number;
  avgWeightPerRep: number;
};

export type StrongestExercisesOverviewDto = {
  muscleGroups: StrongestExerciseByMuscleGroupDto[];
  muscles: StrongestExerciseForMuscleDto[];
};

export type WeeklyVolumeChangeDto = {
  currentWeek: WeeklyVolumeDto;
  previousWeek: WeeklyVolumeDto;
  volumeChange: number;
  percentageChange: number | null;
};

export type WeeklyVolumeAnalysisDto = {
  muscleId: number | null;
  muscleGroup: MuscleGroup | null;
  targetName: string;
  numWeeksBack: number;
  weeklyChanges: WeeklyVolumeChangeDto[];
};

export type WeeklyOneRepMaxDto = {
  startDate: string;
  endDate: string;
  oneRepMax: number | null;
  topSet: { id: number; weight: number; reps: number } | null;
  workoutId: number | null;
  workoutDate: string | null;
};

export type WeeklyOneRepMaxChangeDto = {
  currentWeek: WeeklyOneRepMaxDto;
  previousWeek: WeeklyOneRepMaxDto;
  oneRepMaxChange: number | null;
  percentageChange: number | null;
};

export type ExerciseWeeklyOneRepMaxDto = {
  exerciseId: number;
  exerciseName: string;
  weeklyChanges: WeeklyOneRepMaxChangeDto[];
};

export type WeeklyOneRepMaxAnalysisDto = {
  muscleId: number;
  muscleName: string;
  age: number;
  bodyWeight: WeightDto;
  numWeeksBack: number;
  exercises: ExerciseWeeklyOneRepMaxDto[];
};

export type NormalizedStrengthPointDto = {
  weekStart: string;
  weekEnd: string;
  averageStrengthIndex: number;
  exerciseCount: number;
};

export type NormalizedStrengthAnalysisDto = {
  muscleId: number | null;
  muscleGroup: MuscleGroup | null;
  targetName: string;
  numWeeksBack: number;
  trend: NormalizedStrengthPointDto[];
};
