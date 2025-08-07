import HomePage from "./component/homepage/HomePage";
import { Routes, Route } from 'react-router-dom';
import LoginForm from './component/login/LoginForm';
import { Provider } from 'react-redux';
import { store } from './store/store';
import AppInitializer from './component/AppInitializer';
import RegisterForm from './component/register/RegisterForm';
import ProductDetail from './component/product/ProductDetail';
import ShopAdmin from './component/admin/ShopAdmin';
import LogoutModal from './component/common/LogoutModal';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import React from 'react';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Checkout from './component/product/Checkout';
import { useAppDispatch } from './store/hooks';
import { clearUser } from './store/features/userSlice';
import PaymentSuccess from './component/payment/PaymentSuccess';
import Cart from './component/product/Cart';
import SearchResultPage from './component/homepage/SearchResultPage';
import UserProfileEdit from './component/homepage/UserProfileEdit';

function AppContent() {
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  useEffect(() => {
    // Kiểm tra token khi component mount
    const token = localStorage.getItem('token');
    
    // Chỉ kiểm tra nếu có token (user đã đăng nhập)
    if (token) {
      try {
        const tokenData = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Date.now() / 1000;
        if (tokenData.exp < currentTime) {
          setShowLogoutModal(true);
          return;
        }
      } catch (error) {
        // Token không hợp lệ (có token nhưng format sai)
        setShowLogoutModal(true);
        return;
      }
    }

    const handleTokenExpired = () => {
      setShowLogoutModal(true);
    };

    window.addEventListener('tokenExpired', handleTokenExpired);
    
    return () => {
      window.removeEventListener('tokenExpired', handleTokenExpired);
    };
  }, []);

  const handleLogoutConfirm = () => {
    // Xóa dữ liệu khi user xác nhận
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    dispatch(clearUser());
    setShowLogoutModal(false);
    navigate('/login');
  };

  const handleLogoutCancel = () => {
    // Xóa dữ liệu khi user đóng modal
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    dispatch(clearUser());
    setShowLogoutModal(false);
    navigate('/login');
  };

  return (
    <>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginForm />} />
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/product/:id" element={<ProductDetail />} /> 
        <Route path="/admin" element={<ShopAdmin />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/payment/success" element={<PaymentSuccess />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/search" element={<SearchResultPage />} />
        <Route path="/profile" element={<UserProfileEdit />} />
      </Routes>
      
      <LogoutModal
        isOpen={showLogoutModal}
        onClose={handleLogoutCancel}
        onConfirm={handleLogoutConfirm}
        title="Phiên đăng nhập hết hạn"
        message="Phiên đăng nhập của bạn đã hết hạn. Bạn cần đăng nhập lại để tiếp tục sử dụng."
        cancelText="Đóng"
        confirmText="Đăng nhập lại"
      />
    </>
  );
}

function App() {
  return (
    <Provider store={store}>
      <AppInitializer>
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={true}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="colored"
        />
        <AppContent />
      </AppInitializer>
    </Provider>
  );
}

export default App;
