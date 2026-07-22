package com.pxbzi.workout_tracker.exercises.models;

import jakarta.persistence.*;
import com.pxbzi.workout_tracker.muscles.models.Muscle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(indexes = {@Index(name = "idx_exercise_name", columnList = "name"),
        @Index(name = "idx_exercise_type", columnList = "exercise_type"),
        @Index(name = "idx_exercise_primary_muscle_id", columnList = "primary_muscle_id")})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type")
    private ExerciseType exerciseType;

    @OneToMany(mappedBy = "exercise",  cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<ExerciseMuscle> musclesWorked;

    @ManyToOne
    @JoinColumn(name = "primary_muscle_id", nullable = false)
    private Muscle primaryMuscle;

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

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
