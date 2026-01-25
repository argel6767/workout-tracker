package com.pxbzi.workout_tracker.weights;

import com.pxbzi.workout_tracker.weights.models.Weight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeightRepository extends JpaRepository<Weight,Long> {

    List<Weight> findByEntryDateBetweenOrderByEntryDateAsc(LocalDate start, LocalDate end);
}
