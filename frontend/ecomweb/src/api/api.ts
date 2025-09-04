// Gửi thông báo hệ thống
export const sendNotification = (payload: {
  recipientId: string[],
  type: string,
  title: string,
  message: string
}) => {
  return axiosInstance.post('/notifications/send', payload);
};
// Kiểm tra phòng chat riêng giữa 2 user (user1 < user2)
export const checkPrivateChat = (user1: string, shopId: string) => {
  return axiosInstance.get(`/private_chats/shop`, { params: { user1, shopId } });
};
// Tạo private chat với user2 (shop owner)
export const createPrivateChat = (shopId: string) => {
  return axiosInstance.post(`/private_chats?shopId=${shopId}`);
};
// Lấy danh sách sản phẩm theo shopId
export const getProductsByShopId = (shopId: string) => {
  return axiosInstance.get(`/products/shop/${shopId}`);
};
// Lấy thông tin shop theo id
export const getShopById = (shopId: string) => {
  return axiosInstance.get(`/shop/${shopId}`);
};
// Lấy danh sách tin nhắn của 1 phòng
export const fetchMessagesByRoomId = (roomId: string) => {
  return axiosInstance.get(`/messages/${roomId}`);
};

// Gửi tin nhắn
export const sendMessage = (message: any) => {
  return axiosInstance.post('/messages', message);
};
import axiosInstance from './axiosInstance';

// Lấy danh sách phòng chat của user
// Lấy danh sách phòng chat của user, trả về kèm roomName nếu có
export const fetchUserRooms = () => {
  return axiosInstance.get('/user_rooms');
};

// Lấy tên phòng theo roomId (chỉ gọi khi user_room không có roomName)
export const getRoomNameByRoomId = async (roomId: string) => {
  const res = await axiosInstance.get(`/rooms/${roomId}`);
  return res.data?.result?.roomName || '';
};

// Lấy chi tiết phòng theo roomId
export const fetchRoomById = (roomId: string) => {
  return axiosInstance.get(`/rooms/${roomId}`);
};
// Xác thực tài khoản (verify email)
export const verifyAccount = (token: string, username: string) => {
  return axiosInstance.post('/users/verification', null, { params: { token, username } });
};
// Notification API

export async function fetchUserNotifications(userId: string) {
  const res = await axiosInstance.get(`/notifications/${userId}`);
  return res.data;
}

export async function markNotificationAsRead(notificationRecipientId: string) {
  return axiosInstance.post(`/notifications/read/${notificationRecipientId}`);
}
export const searchProductByImage = (file: File, page = 0, size = 8) => {
  const formData = new FormData();
  formData.append('image', file);
  formData.append('page', String(page));
  formData.append('size', String(size));
  return axiosInstance.post('/weaviate/search', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};
export const updateShop = (shopUpdateRequest: any) => {
  return axiosInstance.put('/shop', shopUpdateRequest);
};
// Cập nhật địa chỉ user
export const updateUserAddress = (id: string, addressData: any) => {
  return axiosInstance.put(`/user_address/${id}`, addressData);
};
// Xóa địa chỉ user
export const deleteUserAddress = (id: string) => {
  return axiosInstance.delete(`/user_address/${id}`);
};
// Cập nhật thông tin user
export const updateUser = (userData: any) => {
  return axiosInstance.put('/users/my-info', userData);
};


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

export const registerShop = (shopData: any) => {
  return axiosInstance.post('/shop', shopData);
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

export const getCartItems = () => {
  return axiosInstance.get('/carts');
};

export const addToCart = (productVariantId: string, quantity: number) => {
  return axiosInstance.post('/carts', {
    productVariantId,
    quantity
  });
};

export const searchProduct = (params: { page: number; size: number; search?: string; status?: number; minPrice?: number; maxPrice?: number }) => {
  return axiosInstance.get('/products/searching', { params });
};

export const getShopInfoByUserId = () => {
  return axiosInstance.get(`/shop`);
};
