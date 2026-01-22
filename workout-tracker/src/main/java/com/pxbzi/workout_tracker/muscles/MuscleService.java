package com.pxbzi.workout_tracker.muscles;

import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.muscles.models.NewMuscleDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MuscleService {

    private final MuscleRepository muscleRepository;

    public MuscleDto createMuscle(NewMuscleDto newMuscleDto) {
        Muscle muscle = new Muscle(newMuscleDto.name(), newMuscleDto.muscleGroup());
        muscle =  muscleRepository.save(muscle);
        return MuscleDto.getMuscleDTO(muscle);
    }

    public List<MuscleDto> bulkCreateMuscles(List<NewMuscleDto> newMuscleDtos) {
        List<Muscle> newMuscles = newMuscleDtos.stream()
                .map(dto -> new Muscle(dto.name(), dto.muscleGroup()))
                .toList();

        return muscleRepository.saveAll(newMuscles).stream()
                .map(MuscleDto::getMuscleDTO)
                .toList();

    }

    public MuscleDto getMuscle(Long id) {
        Muscle muscle = muscleRepository.findById(id).orElseThrow();
        return MuscleDto.getMuscleDTO(muscle);
    }

    public List<MuscleDto> getAllMuscles() {
        return muscleRepository.findAll().stream()
                .map(MuscleDto::getMuscleDTO)
                .toList();
    }

    public MuscleDto updateMuscle(Long id, MuscleDto muscleDto) {
        Muscle muscle = muscleRepository.findById(id).orElseThrow();
        muscle.setName(muscle.getName());
        muscle.setMuscleGroup(muscle.getMuscleGroup());
        Muscle muscleUpdated = muscleRepository.save(muscle);
        return MuscleDto.getMuscleDTO(muscleUpdated);
    }

    public void deleteMuscleById(Long id) {
        muscleRepository.deleteById(id);
    }
}
