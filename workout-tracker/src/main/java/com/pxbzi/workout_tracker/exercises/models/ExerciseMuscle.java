package com.pxbzi.workout_tracker.exercises.models;

import com.pxbzi.workout_tracker.muscles.models.Muscle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "exercise_muscles_worked",
        indexes = {
        @Index(name = "idx_exercise_id", columnList = "exercise_id"),
        @Index(name = "idx_muscle_id", columnList = "muscle_id")
        })
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseMuscle {

    public ExerciseMuscle(Exercise exercise, Muscle muscle) {
        this.exercise = exercise;
        this.muscle = muscle;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, updatable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "muscle_id", nullable = false)
    private Muscle muscle;
}
