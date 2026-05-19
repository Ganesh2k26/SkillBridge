/** Extract a user-friendly message from an Axios error. */
export function getApiErrorMessage(err, fallback = 'Request failed') {
  if (!err?.response) {
    return 'Cannot reach server. Start the backend: cd backend && mvn spring-boot:run';
  }
  const data = err.response.data;
  if (typeof data?.message === 'string' && data.message) return data.message;
  if (data?.fieldErrors && typeof data.fieldErrors === 'object') {
    return Object.values(data.fieldErrors).join('; ');
  }
  if (typeof data?.error === 'string') return data.error;
  return fallback;
}
