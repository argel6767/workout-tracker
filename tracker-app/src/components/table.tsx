import { useState } from "react";
import { ExerciseData } from "./exercise-data";
import { useGetWorkoutsByExercise } from "../hooks/useGetWorkoutsByExercise";

type TableProps<T extends Record<string, unknown>> = {
  data: T[];
};

export const TableContainer = () => {
  const [exerciseId, setExerciseId] = useState<number>(1);
  const handleExerciseChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setExerciseId(Number(e.target.value));
  };

  const { data, isLoading, isError } = useGetWorkoutsByExercise(exerciseId);

  const renderTableContent = () => {
    if (isLoading)
      return <span className="loading loading-dots loading-xl"></span>;
    if (isError) return <div>Error loading exercises</div>;
    if (!data) return <div>No data found</div>;
    return <Table data={data} />;
  };

  return (
    <main className="hero">
      <span className="hero-content text-center">
        <div className="flex flex-col gap-4">
          <ExerciseData handleExerciseChange={handleExerciseChange} />
          {renderTableContent()}
        </div>
      </span>
    </main>
  );
};

/**
 * Renders a cell value, handling primitives, objects, and arrays appropriately
 */
const renderCellValue = (value: unknown): React.ReactNode => {
  // Handle null and undefined
  if (value === null || value === undefined) {
    return <span className="text-gray-400">-</span>;
  }

  // Handle primitives (string, number, boolean)
  if (
    typeof value === "string" ||
    typeof value === "number" ||
    typeof value === "boolean"
  ) {
    return String(value);
  }

  // Handle arrays
  if (Array.isArray(value)) {
    if (value.length === 0) {
      return <span className="text-gray-400">Empty</span>;
    }

    // Check if it's an array of objects
    if (typeof value[0] === "object" && value[0] !== null) {
      return (
        <div className="flex flex-col gap-1">
          {value.map((item, index) => (
            <div key={index} className="badge badge-outline badge-sm">
              {renderObjectSummary(item as Record<string, unknown>)}
            </div>
          ))}
        </div>
      );
    }

    // Array of primitives
    return value.map(String).join(", ");
  }

  // Handle objects
  if (typeof value === "object") {
    return (
      <div className="badge badge-primary badge-sm">
        {renderObjectSummary(value as Record<string, unknown>)}
      </div>
    );
  }

  // Fallback
  return String(value);
};

/**
 * Renders a summary of an object, showing its most meaningful property
 */
const renderObjectSummary = (obj: Record<string, unknown>): string => {
  // Prefer 'name' property if it exists
  if ("name" in obj && typeof obj.name === "string") {
    return obj.name;
  }

  // Fallback to showing key properties for common patterns
  if ("weight" in obj && "reps" in obj) {
    return `${obj.weight}lbs x ${obj.reps}`;
  }

  // Show id if available
  if ("id" in obj) {
    return `ID: ${obj.id}`;
  }

  // Last resort: show first string/number value
  for (const key of Object.keys(obj)) {
    const val = obj[key];
    if (typeof val === "string" || typeof val === "number") {
      return String(val);
    }
  }

  return "[Object]";
};

const Table = <T extends Record<string, unknown>>({ data }: TableProps<T>) => {
  if (!data.length) {
    return <p className="text-center p-4">No data available</p>;
  }

  const headers = Object.keys(data[0]);

  return (
    <main>
      <table className="table">
        <thead>
          <tr>
            {headers.map((header) => (
              <th key={header}>{formatHeader(header)}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr key={index}>
              {headers.map((header) => (
                <td key={header}>{renderCellValue(row[header])}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </main>
  );
};

/**
 * Formats a camelCase header to a more readable format
 */
const formatHeader = (header: string): string => {
  return header
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (str) => str.toUpperCase())
    .trim();
};
