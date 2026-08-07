import { useEffect, useState } from "react";

export type SubmissionStatus = {
  type: "success" | "error";
  message: string;
} | null;

const FEEDBACK_DURATION_MS = 3500;

export const useSubmissionStatus = () => {
  const [status, setStatus] = useState<SubmissionStatus>(null);

  useEffect(() => {
    if (!status) return;

    const timeoutId = window.setTimeout(() => setStatus(null), FEEDBACK_DURATION_MS);
    return () => window.clearTimeout(timeoutId);
  }, [status]);

  return { status, setStatus };
};
