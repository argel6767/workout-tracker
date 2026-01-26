import { apiClient } from "./apiConfig";
import type { NewWeightDto, WeightDto } from "../lib/form-dtos";

const V1_WEIGHTS = "/v1/weights";

export const getWeightsByDate = async (numMonthsBack: number): Promise<WeightDto[]> => {
  const response = await apiClient.get(`${V1_WEIGHTS}/dates`, {
    params: {
      numMonthsBack,
    },
  });
  return response.data;
};

export const getWeight = async (weightId: number): Promise<WeightDto> => {
  const response = await apiClient.get(`${V1_WEIGHTS}/${weightId}`);
  return response.data;
};

export const createWeight = async (weightDto: NewWeightDto): Promise<WeightDto> => {
  const response = await apiClient.post(`${V1_WEIGHTS}`, weightDto);
  return response.data;
}

export const updateWeight = async (weightDto: WeightDto): Promise<WeightDto> => {
  const response = await apiClient.put(`${V1_WEIGHTS}/${weightDto.id}`, weightDto);
  return response.data;
}

export const deleteWeight = async (weightId: number): Promise<void> => {
  await apiClient.delete(`${V1_WEIGHTS}/${weightId}`);
}
