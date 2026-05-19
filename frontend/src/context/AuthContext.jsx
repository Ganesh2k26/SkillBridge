import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import API from '../api/axiosConfig';

const Ctx = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser]     = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem('sb_user');
    const token  = localStorage.getItem('sb_token');
    if (stored && token) setUser(JSON.parse(stored));
    setLoading(false);
  }, []);

  const persist = (data) => {
    if (!data?.token) throw new Error('No token received from server');
    localStorage.setItem('sb_token', data.token);
    const u = { name: data.name, email: data.email, role: data.role, targetCompany: data.targetCompany };
    localStorage.setItem('sb_user', JSON.stringify(u));
    setUser(u);
    return u;
  };

  const login = async (email, password) => {
    localStorage.removeItem('sb_token');
    localStorage.removeItem('sb_user');
    const res = await API.post('/auth/login', {
      email: email.trim().toLowerCase(),
      password,
    });
    return persist(res.data);
  };

  const register = async (payload) => {
    localStorage.removeItem('sb_token');
    localStorage.removeItem('sb_user');
    const body = {
      ...payload,
      name: payload.name?.trim(),
      email: payload.email?.trim().toLowerCase(),
      targetCompany: payload.targetCompany || null,
      collegeName: payload.collegeName || null,
      graduationYear: payload.graduationYear || null,
    };
    const res = await API.post('/auth/register', body);
    return persist(res.data);
  };

  const logout = useCallback(() => { localStorage.clear(); setUser(null); }, []);

  return <Ctx.Provider value={{ user, loading, login, register, logout }}>{children}</Ctx.Provider>;
}

export const useAuth = () => useContext(Ctx);
