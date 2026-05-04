import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../services/api';

interface AuthContextType {
  isAuthenticated: boolean;
  username: string | null;
  accountId: number | null;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  token: string | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState<string | null>(null);
  const [accountId, setAccountId] = useState<number | null>(null);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUsername = localStorage.getItem('username');
    const storedAccountId = localStorage.getItem('accountId');
    if (storedToken && storedUsername) {
      setToken(storedToken);
      setUsername(storedUsername);
      setAccountId(storedAccountId ? parseInt(storedAccountId) : null);
      setIsAuthenticated(true);
    }
  }, []);

  const login = async (credentials: LoginRequest) => {
    const response: AuthResponse = await api.login(credentials);
    localStorage.setItem('token', response.token);
    localStorage.setItem('username', response.username);
    localStorage.setItem('accountId', String(response.accountId));
    setToken(response.token);
    setUsername(response.username);
    setAccountId(response.accountId);
    setIsAuthenticated(true);
  };

  const register = async (data: RegisterRequest) => {
    const response: AuthResponse = await api.register(data);
    localStorage.setItem('token', response.token);
    localStorage.setItem('username', response.username);
    localStorage.setItem('accountId', String(response.accountId));
    setToken(response.token);
    setUsername(response.username);
    setAccountId(response.accountId);
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('accountId');
    setToken(null);
    setUsername(null);
    setAccountId(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, accountId, login, register, logout, token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
