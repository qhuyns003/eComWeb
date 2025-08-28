import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8082/', // Thay đổi base URL của bạn ở đây
    headers: {
        'Content-Type': 'application/json',
    }
});

//interceptor Thêm/xử lý logic chung cho tất cả các API call mà không cần lặp lại code ở từng chỗ.

// // Thêm interceptor cho response để xử lý token hết hạn
// axiosInstance.interceptors.response.use(
//     (response) => response,
//     (error) => {
//         if (error.response && (error.response.status === 401 || error.response.status === 403)) {
//             // Xóa token khỏi localStorage
//             localStorage.removeItem('token');
            
//             // Dispatch custom event để component khác có thể xử lý
//             const logoutEvent = new CustomEvent('tokenExpired', {
//                 detail: { message: 'Phiên đăng nhập đã hết hạn' }
//             });
//             window.dispatchEvent(logoutEvent);
//         }
//         return Promise.reject(error);
//     }
// );

// Thêm interceptor cho request để tự động gắn token nếu có
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers = config.headers || {};
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export default axiosInstance; 