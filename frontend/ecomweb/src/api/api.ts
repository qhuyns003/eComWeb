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

export const getCategories = () => {
  return axiosInstance.get('/categories/');
};

export const getTopSellingProducts = (limit: number = 8) => {
  return axiosInstance.get(`/products/top-selling?limit=${limit}`);
};

export const getNewestProducts = (limit: number = 8) => {
  return axiosInstance.get(`/products/newest?limit=${limit}`);
};

export const getProductDetail = (id: string) => {
  return axiosInstance.get(`/products/${id}`);
};

export const getProductAttributes = (productId: string) => {
  return axiosInstance.get(`/products/${productId}`);
};

export const getProductReviews = (productId: string) => {
  return axiosInstance.get(`/customer_reviews/${productId}`);
};

export const getProductReviewStats = (productId: string) => {
  return axiosInstance.get(`/customer_reviews/stat/${productId}`);
};

export const getProductsByUserId = (userId: string, page = 0, size = 10, search = '', status = '') => {
  let url = `/products/shop_list_product/${userId}?page=${page}&size=${size}`;
  if (search) url += `&search=${encodeURIComponent(search)}`;
  if (status === 'active') url += `&status=1`;
  if (status === 'inactive') url += `&status=0`;
  return axiosInstance.get(url);
};

export const getProductDetailForEdit = (productId: string) => {
  return axiosInstance.get(`/products/editing_product/${productId}`);
};

export const updateProduct = (productId: string, productData: any) => {
  return axiosInstance.put(`/products/${productId}`, productData);
};

export const uploadProductImage = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return axiosInstance.post('/files/upload_image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

export const deleteProductImage = (imageUrl: string) => {
  return axiosInstance.delete('/files/', { params: { url: imageUrl } });
};

export const createProduct = (productData: any) => {
  return axiosInstance.post('/products', productData);
};

export const deleteProducts = (ids: string[]) => {
  return axiosInstance.delete('/products', { data: ids });
};

export const getUserAddresses = () => {
  return axiosInstance.get('http://localhost:8080/user_address/').then(res => {
    if (res.data && res.data.code === 1000 && Array.isArray(res.data.result)) {
      return res.data.result;
    }
    return [];
  });
};

export const addUserAddress = (addressData: any) => {
  return axiosInstance.post('/user_address/', addressData);
};

export const getProvinces = () => axiosInstance.get('/ghn/provinces');
export const getDistricts = (provinceId: number) => axiosInstance.get('/ghn/districts', { params: { provinceId } });
export const getWards = (districtId: number) => axiosInstance.get('/ghn/wards', { params: { districtId } });

export const getGhnServiceForOrderGroup = (orderGroupPayload: any) => {
  // Gửi về endpoint backend, ví dụ /api/shipping/ghn-service
  return axiosInstance.post('/ghn/available-service', orderGroupPayload);
};

export const calculateShippingFee = (payload: any) => {
  return axiosInstance.post('/ghn/calculate-fee', payload);
};

export const getShopInfo = (shopIds: string[]) => {
  // Gửi shopIds dưới dạng query parameters
  const params = new URLSearchParams();
  shopIds.forEach(id => params.append('ids', id));
  return axiosInstance.get(`/shop_address/?${params.toString()}`);
};

export const getCouponsByShopId = (shopId: string) => {
  return axiosInstance.get(`/coupons/shop?shopId=${shopId}`);
};

export const getUserOrderCoupons = () => axiosInstance.get('/coupons/user');

export const createOrder = (orderData: any) => {
  return axiosInstance.post('/orders', orderData);
};

export const getOrderPaymentStatus = (orderId: string) =>
  axiosInstance.get(`/vnpay/payment-status?vnp_TxnRef=${orderId}`);

export const getPaymentMethods = () => {
  return axiosInstance.get('/payments');
};

export const createPayment = (paymentData: any) => {
  return axiosInstance.post('/vnpay/create-payment', paymentData);
};
