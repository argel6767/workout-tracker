package com.pxbzi.workout_tracker.workouts.models;

import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.user.User;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    indexes = {
        @Index(name = "idx_workout_exercise_id", columnList = "exercise_id"),
        @Index(name = "idx_workout_date", columnList = "workout_date"),
        @Index(name = "idx_workout_user_id", columnList = "user_id")
    }
)
@Data
@NoArgsConstructor
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "workout_date")
    private LocalDate workoutDate;

    @OneToMany(
        mappedBy = "workout",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<WorkoutSet> workoutSets;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (workoutDate == null) {
            workoutDate = LocalDate.now(ZoneId.of("America/New_York"));
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
