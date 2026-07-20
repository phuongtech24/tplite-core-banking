import { apiClient, unwrapData } from '../../services/apiClient';

export const authApi = {
  login: async (payload) => unwrapData(await apiClient.post('/auth/login', payload)),
  register: async (payload) => unwrapData(await apiClient.post('/auth/register', payload)),
  me: async () => unwrapData(await apiClient.get('/me')),
  logout: async (refreshToken) => unwrapData(await apiClient.post('/auth/logout', { refreshToken })),
};
