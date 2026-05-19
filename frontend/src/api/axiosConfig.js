import axios from 'axios';

const baseURL = import.meta.env.VITE_API_URL || '/api';

const API = axios.create({ baseURL, timeout: 60000 });

API.interceptors.request.use(config => {
  const token = localStorage.getItem('sb_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
}, err => Promise.reject(err));

API.interceptors.response.use(
  res => res,
  err => {
    const url = err.config?.url || '';
    const isAuthRoute =
      url.includes('/auth/login') ||
      url.includes('/auth/register') ||
      url.includes('/auth/health');

    if (err.response?.status === 401 && !isAuthRoute) {
      localStorage.removeItem('sb_token');
      localStorage.removeItem('sb_user');
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(err);
  }
);

export default API;
