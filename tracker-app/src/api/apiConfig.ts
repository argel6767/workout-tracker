import axios from 'axios';

export const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    // Add this line to ensure cookies are sent with cross-origin requests
    //withCredentials: true
});