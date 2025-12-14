import { api } from './api';
import type { AuthResponse, LoginRequest, RegistroRequest, ClientePerfil } from '@/types/entities';

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/cliente/login', data);
    if (response.token) {
      localStorage.setItem('token', response.token);
      localStorage.setItem('clienteId', String(response.clienteId));
    }
    return response;
  },

  registro: async (data: RegistroRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/cliente/registro', data);
    if (response.token) {
      localStorage.setItem('token', response.token);
      localStorage.setItem('clienteId', String(response.clienteId));
    }
    return response;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('clienteId');
    window.location.href = '/login';
  },

  getPerfil: () => api.get<ClientePerfil>('/cliente/perfil'),

  isAuthenticated: () => !!localStorage.getItem('token'),

  getClienteId: () => {
    const id = localStorage.getItem('clienteId');
    return id ? parseInt(id, 10) : null;
  },
};

export default authService;

