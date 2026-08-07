import type { SubmissionStatus } from "../hooks/useSubmissionStatus";

export const SubmissionFeedback = ({ status }: { status: SubmissionStatus }) => {
  if (!status) return null;

  return (
    <div
      className={`alert ${status.type === "success" ? "alert-success" : "alert-error"}`}
      role={status.type === "success" ? "status" : "alert"}
    >
      {status.message}
    </div>
  );
};
