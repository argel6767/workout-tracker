import { apiClient } from "./apiConfig";
import type { MuscleDto, NewMuscleDto } from "../lib/dtos";

const V1_MUSCLE_ENDPOINT = "/v1/muscles";

export const getMuscles = async (): Promise<MuscleDto[]> => {
  const response = await apiClient.get(V1_MUSCLE_ENDPOINT);
  return response.data;
};

export const getMuscle = async (muscleId: string): Promise<MuscleDto> => {
  const response = await apiClient.get(`${V1_MUSCLE_ENDPOINT}/${muscleId}`);
  return response.data;
};

export const createMuscle = async (muscle: NewMuscleDto): Promise<MuscleDto> => {
  const response = await apiClient.post(V1_MUSCLE_ENDPOINT, muscle);
  return response.data;
};

export const updateMuscle = async (muscleId: string, muscle: NewMuscleDto): Promise<MuscleDto> => {
  const response = await apiClient.put(`${V1_MUSCLE_ENDPOINT}/${muscleId}`, muscle);
  return response.data;
};

export const deleteMuscle = async (muscleId: string): Promise<void> => {
  await apiClient.delete(`${V1_MUSCLE_ENDPOINT}/${muscleId}`);
};
