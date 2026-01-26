import { useMemo, useState } from "react";
import type {
  NewExerciseDto,
  NewMuscleDto,
  NewSetDto,
  NewWorkoutDto,
  NewWeightDto,
  MuscleGroup,
} from "../lib/form-dtos";
import { useGetMuscles } from "../hooks/useGetMuscles";
import { useGetExercises } from "../hooks/useGetExercises";
import { createWorkout } from "../api/workouts";
import { createMuscle } from "../api/muscles";
import { createExercise } from "../api/exercise";
import { createWeight } from "../api/weights";

export const WorkoutForm = () => {
  const [newWorkout, setNewWorkout] = useState<NewWorkoutDto>({
    exerciseId: -1,
    sets: [],
  });

  const { data = [], isLoading, isError } = useGetExercises();
  const [muscleGroup, setMuscleGroup] = useState<MuscleGroup>("CHEST");

  const exercises = useMemo(() => {
    return data.filter(
      (exercise) => exercise.primaryMuscleGroup === muscleGroup,
    );
  }, [data, muscleGroup]);

  const handleExerciseChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setNewWorkout((prev) => ({
      ...prev,
      exerciseId: Number(e.target.value),
    }));
  };

  const addSet = () => {
    setNewWorkout((prev) => ({
      ...prev,
      sets: [...prev.sets, { weight: 0, reps: 0 }],
    }));
  };

  const removeSet = (index: number) => {
    setNewWorkout((prev) => ({
      ...prev,
      sets: prev.sets.filter((_, i) => i !== index),
    }));
  };

  const updateSet = (index: number, field: keyof NewSetDto, value: number) => {
    setNewWorkout((prev) => ({
      ...prev,
      sets: prev.sets.map((set, i) =>
        i === index ? { ...set, [field]: value } : set,
      ),
    }));
  };

  const handleMuscleGroupChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setMuscleGroup(e.target.value as MuscleGroup);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await createWorkout(newWorkout);
      console.log("Workout created:", response);
      setNewWorkout({
        exerciseId: -1,
        sets: []
      })
    } catch (error) {
      console.error("Error creating workout:", error);
    }
  };

  if (isLoading)
    return <span className="loading loading-dots loading-xl"></span>;
  if (isError) return <div>Error loading table data</div>;

  return (
    <main>
      <h1 className="py-2 text-lg">Add a Workout</h1>
      <form
        className="flex flex-col gap-4 justify-center items-center"
        onSubmit={handleSubmit}
      >
        <label className="input">
          <span className="label">Muscle Group</span>
          <select
            value={muscleGroup}
            onChange={handleMuscleGroupChange}
            className="bg-base-200 rounded-lg p-1"
          >
            <option value="CHEST">Chest</option>
            <option value="BACK">Back</option>
            <option value="SHOULDERS">Shoulders</option>
            <option value="LEGS">Legs</option>
            <option value="ARMS">Arms</option>
            <option value="CORE">Core</option>
          </select>
        </label>
        <label className="input">
          <span className="label">Exercise</span>
          <select
            value={newWorkout.exerciseId}
            onChange={handleExerciseChange}
            className="bg-base-200 rounded-lg p-1"
          >
            {exercises!.map((exercise) => (
              <option key={exercise.id} value={exercise.id}>
                {exercise.name}
              </option>
            ))}
          </select>
        </label>

        <fieldset className="w-full">
          <legend className="font-semibold mb-2">Sets</legend>
          <div className="flex flex-col gap-2">
            {newWorkout.sets.map((set, index) => (
              <div key={index} className="flex gap-2 items-center">
                <label className="input input-sm">
                  <span className="label">Weight</span>
                  <input
                    type="number"
                    value={set.weight}
                    onChange={(e) =>
                      updateSet(index, "weight", Number(e.target.value))
                    }
                    step={0.5}
                  />
                </label>
                <label className="input input-sm">
                  <span className="label">Reps</span>
                  <input
                    type="number"
                    value={set.reps}
                    onChange={(e) =>
                      updateSet(index, "reps", Number(e.target.value))
                    }
                    min={0}
                  />
                </label>
                <button
                  type="button"
                  className="btn btn-error btn-sm"
                  onClick={() => removeSet(index)}
                >
                  Remove
                </button>
              </div>
            ))}
          </div>
          <button
            type="button"
            className="btn btn-secondary btn-sm mt-2"
            onClick={addSet}
          >
            + Add Set
          </button>
        </fieldset>

        <button type="submit" className="btn btn-primary">
          Add Workout
        </button>
      </form>
    </main>
  );
};

export const MuscleForm = () => {
  const [newMuscle, setNewMuscle] = useState<NewMuscleDto>({
    name: "",
    muscleGroup: "CHEST",
  });

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewMuscle((prev) => ({ ...prev, name: e.target.value }));
  };

  const handleMuscleGroupChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setNewMuscle((prev) => ({
      ...prev,
      muscleGroup: e.target.value as NewMuscleDto["muscleGroup"],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await createMuscle(newMuscle);
      console.log("Muscle created:", response);
      setNewMuscle({
        name: "",
        muscleGroup: "CHEST",
      });
    } catch (error) {
      console.error("Error creating muscle:", error);
    }
  };

  return (
    <main>
      <h1 className="py-2 text-lg">Add a Muscle</h1>
      <form
        className="flex flex-col gap-4 justify-center items-center"
        onSubmit={handleSubmit}
      >
        <label className="input">
          <span className="label">Muscle Name</span>
          <input
            type="text"
            placeholder="Biceps"
            value={newMuscle.name}
            onChange={handleNameChange}
          />
        </label>
        <label className="input">
          <span className="label">Muscle Group</span>
          <select
            value={newMuscle.muscleGroup}
            onChange={handleMuscleGroupChange}
            className="bg-base-200 rounded-lg p-1"
          >
            <option value="CHEST">Chest</option>
            <option value="BACK">Back</option>
            <option value="SHOULDERS">Shoulders</option>
            <option value="LEGS">Legs</option>
            <option value="ARMS">Arms</option>
            <option value="CORE">Core</option>
          </select>
        </label>
        <button type="submit" className="btn btn-primary">
          Add Muscle
        </button>
      </form>
    </main>
  );
};

export const ExerciseForm = () => {
  const [newExercise, setNewExercise] = useState<NewExerciseDto>({
    name: "",
    description: "",
    musclesWorked: [],
  });

  const { data: availableMuscles, isLoading, isError } = useGetMuscles();

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewExercise((prev) => ({ ...prev, name: e.target.value }));
  };

  const handleDescriptionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewExercise((prev) => ({ ...prev, description: e.target.value }));
  };

  const handleMuscleToggle = (muscleId: number) => {
    setNewExercise((prev) => ({
      ...prev,
      musclesWorked: prev.musclesWorked.includes(muscleId)
        ? prev.musclesWorked.filter((id) => id !== muscleId)
        : [...prev.musclesWorked, muscleId],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await createExercise(newExercise);
      console.log("Exercise created:", response);
      setNewExercise({
        name: "",
        description: "",
        musclesWorked: [],
      });
    } catch (error) {
      console.error("Error creating exercise:", error);
    }
  };

  if (isLoading) {
    return <span className="loading loading-dots loading-xl"></span>;
  }

  if (isError) {
    return <div>Error loading muscles</div>;
  }

  return (
    <main>
      <h1 className="py-2 text-lg">Add an Exercise</h1>
      <form
        className="flex flex-col gap-6 justify-center items-center"
        onSubmit={handleSubmit}
      >
        <label className="input">
          <span className="label">Exercise Name</span>
          <input
            type="text"
            placeholder="Bench Press"
            value={newExercise.name}
            onChange={handleNameChange}
          />
        </label>
        <label className="input">
          <span className="label">Description</span>
          <input
            type="text"
            value={newExercise.description}
            onChange={handleDescriptionChange}
          />
        </label>
        <fieldset className="grid grid-cols-3 gap-4">
          <legend className="label text-lg">Muscles Worked</legend>
          {availableMuscles!.map((muscle) => (
            <label key={muscle.id} className="flex items-center gap-2">
              <input
                type="checkbox"
                name="muscles"
                checked={newExercise.musclesWorked.includes(muscle.id)}
                onChange={() => handleMuscleToggle(muscle.id)}
              />
              {muscle.name}
            </label>
          ))}
        </fieldset>
        <button type="submit" className="btn btn-primary">
          Add Exercise
        </button>
      </form>
    </main>
  );
};

export const WeightForm = () => {
  const [newWeight, setNewWeight] = useState<NewWeightDto>({
    weight: 0,
  });

  const handleWeightChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewWeight((prev) => ({ ...prev, weight: Number(e.target.value) }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await createWeight(newWeight);
      console.log("Weight created:", response);
    } catch (error) {
      console.error("Error creating weight:", error);
    }
  };

  return (
    <main>
      <h1 className="py-2 text-lg">Add a Weight Entry</h1>
      <form
        className="flex flex-col gap-6 justify-center items-center"
        onSubmit={handleSubmit}
      >
        <label className="input">
          <span className="label">Weight (lbs)</span>
          <input
            type="number"
            placeholder="150"
            value={newWeight.weight}
            onChange={handleWeightChange}
            min={0}
            step={0.1}
          />
        </label>
        <button type="submit" className="btn btn-primary">
          Add Weight
        </button>
      </form>
    </main>
  );
};

type FormType = "workout" | "muscle" | "exercise" | "weight";

export const FormContainer = () => {
  const [form, setForm] = useState<FormType>("workout");

  return (
    <div className="hero">
      <div className="hero-content text-center">
        <div className="max-w-md">
          <h1 className="text-4xl font-bold">Insert New Data</h1>
          <div className="py-6">
            {form === "workout" ? (
              <WorkoutForm />
            ) : form === "muscle" ? (
              <MuscleForm />
            ) : form === "exercise" ? (
              <ExerciseForm />
            ) : (
              <WeightForm />
            )}
          </div>
          <span className="flex justify-center gap-4 py-2">
            <button
              className="btn btn-neutral"
              onClick={() => setForm("workout")}
            >
              Add a Workout
            </button>
            <button
              className="btn btn-neutral"
              onClick={() => setForm("muscle")}
            >
              Add a Muscle
            </button>
            <button
              className="btn btn-neutral"
              onClick={() => setForm("exercise")}
            >
              Add an Exercise
            </button>
            <button
              className="btn btn-neutral"
              onClick={() => setForm("weight")}
            >
              Add a Weight
            </button>
          </span>
        </div>
      </div>
    </div>
  );
};
