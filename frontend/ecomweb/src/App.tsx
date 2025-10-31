import HomePage from "./component/homepage/HomePage";
import { Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from './store/store';
import AppInitializer from './component/AppInitializer';
import RegisterForm from './component/register/RegisterForm';
import VerifyAccount from './component/register/VerifyAccount';
import ProductDetail from './component/product/ProductDetail';
import ShopAdmin from './component/admin/ShopAdmin';
import LogoutModal from './component/common/LogoutModal';
import ProtectedRoute from './components/ProtectedRoute';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth, AuthProvider } from './contexts/AuthContext';
import LoadingSpinner from './components/LoadingSpinner';

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ShopInfoEdit from './component/homepage/ShopInfoEdit';
import Checkout from './component/product/Checkout';
import { useAppDispatch } from './store/hooks';
import { clearUser } from './store/features/userSlice';
import PaymentSuccess from './component/payment/PaymentSuccess';
import Cart from './component/product/Cart';
import SearchResultPage from './component/homepage/SearchResultPage';
import UserProfileEdit from './component/homepage/UserProfileEdit';
import RegisterShopForm from './component/homepage/RegisterShopForm';

import ShopDetail from './component/shop/ShopDetail';

function AppContent() {
  const { isAuthenticated, isLoading } = useAuth();
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

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
    // Xóa dữ liệu legacy nếu có
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    dispatch(clearUser());
    setShowLogoutModal(false);
    navigate('/login');
  };

  const handleLogoutCancel = () => {
    setShowLogoutModal(false);
  };

  // Hiển thị loading khi đang khởi tạo Keycloak
  if (isLoading) {
    return <LoadingSpinner />;
  }

  // Nếu chưa authenticated, chỉ hiển thị các route public và redirect về home
  if (!isAuthenticated) {
    return (
      <>
        <Routes>
          <Route path="/register" element={<RegisterForm />} />
          <Route path="/verify" element={<VerifyAccount />} />
          <Route path="*" element={<HomePage />} />
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

  // Authenticated routes
  return (
    <>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<HomePage />} /> {/* Redirect to home nếu đã login */}
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/product/:id" element={<ProductDetail />} /> 
        <Route path="/admin" element={
          <ProtectedRoute requireSeller={true}>
            <ShopAdmin />
          </ProtectedRoute>
        } />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/payment/success" element={<PaymentSuccess />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/search" element={<SearchResultPage />} />
        <Route path="/profile" element={<UserProfileEdit />} />
        <Route path="/register-shop" element={<RegisterShopForm />} />
        <Route path="/shop-info" element={<ShopInfoEdit />} />
        <Route path="/verify" element={<VerifyAccount />} />
        <Route path="/shop/:id" element={<ShopDetail />} />
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
    </>
  );
}

function App() {
  return (
    <Provider store={store}>
      <AuthProvider>
        <AppInitializer>
          <AppContent />
        </AppInitializer>
      </AuthProvider>
    </Provider>
  );
}

export default App;
