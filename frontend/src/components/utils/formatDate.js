export default function formatDate(dateTimeString) {
  const dateObject = new Date(dateTimeString);

  // Define options for formatting the date
  const options = {
    year: "numeric",
    month: "long",
    day: "2-digit", // This ensures '04' instead of '4'
  };

  // Format the date using toLocaleDateString with a locale that puts day first,
  // or use 'en-US' and then manually reorder.
  // For '04 June 2025', 'en-GB' (British English) is a good fit.
  return dateObject.toLocaleDateString("en-GB", options);
}
