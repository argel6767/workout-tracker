import type { Optional } from "../lib/form-dtos";

type EntryDateFormProps = {
  date: Optional<string>,
  onDateChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
}

export const EntryDateForm = ({ date, onDateChange }: EntryDateFormProps) => {
  return (
    <main className="flex flex-col gap-3 py-2">
      <h3 className="text-lg">Add entry date (optional)</h3>
      <input type="date" value={date as string} onChange={onDateChange} />
    </main>
  );
};
