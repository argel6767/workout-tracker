package com.pxbzi.workout_tracker.workouts.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_set_workout_id", columnList = "workout_id")
})
@Data
@NoArgsConstructor
public class Set {

    public Set(Workout workout, Integer reps, Double weight) {
        this.workout = workout;
        this.reps = reps;
        this.weight = weight;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    private Integer reps;

    private Double weight;

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.updatedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
