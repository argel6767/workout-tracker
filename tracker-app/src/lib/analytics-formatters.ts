export const formatAnalyticsLabel = (value: string) =>
  value
    .toLowerCase()
    .replace(/(^|[-_])\w/g, (letter) => letter.toUpperCase())
    .replaceAll("_", " ");
