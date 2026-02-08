package com.pxbzi.workout_tracker.muscles.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(indexes = {@Index(name = "idx_muscle_name", columnList = "name"),
        @Index(name = "idx_muscle_group", columnList = "muscle_group")})
@NoArgsConstructor
public class Muscle {

    public Muscle(String name, MuscleGroup muscleGroup) {
        this.name = name;
        this.muscleGroup = muscleGroup;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_group")
    private MuscleGroup muscleGroup;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
