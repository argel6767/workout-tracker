import axios from 'axios';

export const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
    // Add this line to ensure cookies are sent with cross-origin requests
    //withCredentials: true
});
