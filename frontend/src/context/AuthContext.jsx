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
    localStorage.setItem('sb_token', data.token);
    const u = { name: data.name, email: data.email, role: data.role, targetCompany: data.targetCompany };
    localStorage.setItem('sb_user', JSON.stringify(u));
    setUser(u);
    return u;
  };

  const login    = async (email, password) => persist((await API.post('/auth/login',    { email, password })).data);
  const register = async (payload)         => persist((await API.post('/auth/register', payload)).data);
  const logout   = useCallback(() => { localStorage.clear(); setUser(null); }, []);

  return <Ctx.Provider value={{ user, loading, login, register, logout }}>{children}</Ctx.Provider>;
}

export const useAuth = () => useContext(Ctx);
