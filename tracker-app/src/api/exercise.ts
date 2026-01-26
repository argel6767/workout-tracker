import type { ExerciseDto, NewExerciseDto } from '../lib/form-dtos';
import { apiClient } from './apiConfig';

const V1_EXERCISES = '/v1/exercises';

export const getExercise = async (exerciseId: string): Promise<ExerciseDto> => {
  const response = await apiClient.get(`${V1_EXERCISES}/${exerciseId}`);
  return response.data;
};

export const getExercises = async (): Promise<ExerciseDto[]> => {
  const response = await apiClient.get(`${V1_EXERCISES}`);
  return response.data;
};

export const createExercise = async (exercise: NewExerciseDto): Promise<ExerciseDto> => {
  const response = await apiClient.post(`${V1_EXERCISES}`, exercise);
  return response.data;
};

export const updateExercise = async (exerciseId: string, exercise: NewExerciseDto): Promise<ExerciseDto> => {
  const response = await apiClient.put(`${V1_EXERCISES}/${exerciseId}`, exercise);
  return response.data;
};

export const deleteExercise = async (exerciseId: string): Promise<void> => {
  await apiClient.delete(`${V1_EXERCISES}/${exerciseId}`);
};
