import type { Optional } from "../lib/form-dtos";

type EntryDateFormProps = {
  date: Optional<string>,
  onDateChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
}

export const EntryDateForm = ({ date, onDateChange }: EntryDateFormProps) => {
  return (
    <main className="flex flex-col gap-3 py-2">
      <label className="text-lg" htmlFor="entry-date">Add entry date (optional)</label>
      <input id="entry-date" type="date" value={date ?? ''} onChange={onDateChange} />
    </main>
  );
};
