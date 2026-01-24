import { apiClient } from "./apiConfig";
import type { NewWorkoutDto, WorkoutDto } from "../lib/dtos";

const V1_WORKOUTS = "/v1/workouts";

export const getWorkouts = async (): Promise<WorkoutDto[]> => {
  const response = await apiClient.get(V1_WORKOUTS);
  return response.data;
};

export const getWorkout = async (workoutId: string): Promise<WorkoutDto> => {
  const response = await apiClient.get(`${V1_WORKOUTS}/${workoutId}`);
  return response.data;
};

export const createWorkout = async (workout: NewWorkoutDto): Promise<WorkoutDto> => {
  const response = await apiClient.post(V1_WORKOUTS, workout);
  return response.data;
};

export const updateWorkout = async (workoutId: string, workout: NewWorkoutDto): Promise<WorkoutDto> => {
  const response = await apiClient.put(`${V1_WORKOUTS}/${workoutId}`, workout);
  return response.data;
};

export const deleteWorkout = async (workoutId: string): Promise<void> => {
  await apiClient.delete(`${V1_WORKOUTS}/${workoutId}`);
};
