import axiosInstance from './axiosInstance';

export const login = (username: string, password: string) => {
  return axiosInstance.post('/auth/token', { username, password });
};

export const getMyInfo = (token: string) => {
  return axiosInstance.get('/users/my-info',{headers: {Authorization: `Bearer ${token}`}});
};

export const logout = (token: string) => {
  return axiosInstance.post('/auth/logout', { token });
};

export const register = (userData: any) => {
  return axiosInstance.post('/users', userData);
}; 