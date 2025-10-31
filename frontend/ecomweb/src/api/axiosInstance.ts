import axios from 'axios';
import { getToken, updateToken } from '../services/keycloakService';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/', // Thay đổi base URL của bạn ở đây
    headers: {
        'Content-Type': 'application/json',
    }
});

//interceptor Thêm/xử lý logic chung cho tất cả các API call mà không cần lặp lại code ở từng chỗ.

// Thêm interceptor cho response để xử lý token hết hạn
axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response && error.response.status === 401) {
            // Thử refresh token
            const newToken = await updateToken();
            if (newToken) {
                // Retry request với token mới
                error.config.headers['Authorization'] = `Bearer ${newToken}`;
                return axiosInstance.request(error.config);
            } else {
                // Token không thể refresh, redirect đến login
                const logoutEvent = new CustomEvent('tokenExpired', {
                    detail: { message: 'Phiên đăng nhập đã hết hạn' }
                });
                window.dispatchEvent(logoutEvent);
            }
        }
        return Promise.reject(error);
    }
);

// Thêm interceptor cho request để tự động gắn Keycloak token
axiosInstance.interceptors.request.use(
    async (config) => {
        const token = getToken();
        if (token) {
            config.headers = config.headers || {};
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export default axiosInstance; 