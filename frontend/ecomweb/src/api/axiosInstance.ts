import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/', // Thay đổi base URL của bạn ở đây
    headers: {
        'Content-Type': 'application/json',
    }
});

export default axiosInstance; 