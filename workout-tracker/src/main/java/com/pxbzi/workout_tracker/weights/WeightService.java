package com.pxbzi.workout_tracker.weights;

import com.pxbzi.workout_tracker.weights.models.NewWeightDto;
import com.pxbzi.workout_tracker.weights.models.Weight;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@Data
public class WeightService {

    private final WeightRepository weightRepository;

    public WeightDto createWeight(NewWeightDto weightDto) {
        if (weightDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required request body is missing");
        }
        Weight weight = new Weight();
        if (weightDto.weight() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required weight is missing");
        }
        
        weight.setWeight(weightDto.weight());
        weight.setEntryDate(weightDto.entryDate());
        weight = weightRepository.save(weight);
        return WeightDto.getWeightDto(weight);
    }

    public List<WeightDto> getAllWeights() {
        return weightRepository.findAll().stream()
                .map(WeightDto::getWeightDto)
                .toList();
    }

    public WeightDto getWeight(Long weightId) {
        Weight weight = weightRepository.findById(weightId)
                .orElseThrow();
        return WeightDto.getWeightDto(weight);
    }

    public WeightDto getNewestWeightEntry() {
        Weight weight = weightRepository.findNewest()
                .orElseThrow();
        return WeightDto.getWeightDto(weight);
    }

    public List<WeightDto> getAllWeightsInDateRange(int numMonthsBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(numMonthsBack);
        return weightRepository.findByEntryDateBetweenOrderByEntryDateAsc(startDate, endDate).stream()
                .map(WeightDto::getWeightDto)
                .toList();
    }

    public WeightDto updateWeight(Long weightId, WeightDto weightDto) {
        Weight weight = weightRepository.findById(weightId)
                .orElseThrow();
        weight.setWeight(weightDto.weight());
        weight.setEntryDate(weightDto.entryDate());
        weight = weightRepository.save(weight);
        return WeightDto.getWeightDto(weight);
    }

    public void deleteWeight(Long weightId) {
        weightRepository.deleteById(weightId);
    }
}
