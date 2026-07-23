package com.pxbzi.workout_tracker.muscles;

import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.muscles.models.NewMuscleDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MuscleServiceTest {

    @Test
    void createsMuscle() {
        MuscleRepository repository = mock(MuscleRepository.class);
        when(repository.save(any())).thenAnswer(call -> {
            Muscle muscle = call.getArgument(0);
            muscle.setId(2L);
            return muscle;
        });

        MuscleDto result = new MuscleService(repository)
                .createMuscle(new NewMuscleDto("Chest", MuscleGroup.CHEST));

        assertThat(result).isEqualTo(new MuscleDto(2L, "Chest", MuscleGroup.CHEST));
    }

    @Test
    void getsAllMuscles() {
        MuscleRepository repository = mock(MuscleRepository.class);
        Muscle muscle = muscle(3L, "Lats", MuscleGroup.BACK);
        when(repository.findAll()).thenReturn(List.of(muscle));

        List<MuscleDto> result = new MuscleService(repository).getAllMuscles();

        assertThat(result).containsExactly(new MuscleDto(3L, "Lats", MuscleGroup.BACK));
    }

    @Test
    void updateUsesRequestedValues() {
        MuscleRepository repository = mock(MuscleRepository.class);
        Muscle muscle = muscle(1L, "Old", MuscleGroup.ARMS);
        when(repository.findById(1L)).thenReturn(Optional.of(muscle));
        when(repository.save(muscle)).thenReturn(muscle);

        MuscleDto result = new MuscleService(repository).updateMuscle(
                1L,
                new MuscleDto(1L, "Deltoid", MuscleGroup.SHOULDERS)
        );

        assertThat(result).isEqualTo(new MuscleDto(1L, "Deltoid", MuscleGroup.SHOULDERS));
    }

    @Test
    void deleteDelegatesToRepository() {
        MuscleRepository repository = mock(MuscleRepository.class);

        new MuscleService(repository).deleteMuscleById(9L);

        verify(repository).deleteById(9L);
    }

    private Muscle muscle(Long id, String name, MuscleGroup group) {
        Muscle muscle = new Muscle(name, group);
        muscle.setId(id);
        return muscle;
    }
}
