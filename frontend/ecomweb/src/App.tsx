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

function AppContent() {
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const handleTokenExpired = () => {
      setShowLogoutModal(true);
    };

    window.addEventListener('tokenExpired', handleTokenExpired);
    
    return () => {
      window.removeEventListener('tokenExpired', handleTokenExpired);
    };
  }, []);

  const handleLogoutConfirm = () => {
    setShowLogoutModal(false);
    navigate('/login');
  };

  const handleLogoutCancel = () => {
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
