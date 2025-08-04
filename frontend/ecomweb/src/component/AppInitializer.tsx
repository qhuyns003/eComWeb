import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { fetchUserInfo, selectUserLoading, selectUser } from '../store/features/userSlice';

function isTokenExpired(token: string | null) {
  if (!token) return true;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    if (!payload.exp) return false;
    return Date.now() >= payload.exp * 1000;
  } catch {
    return true;
  }
}

const AppInitializer: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const dispatch = useAppDispatch();
  const loading = useAppSelector(selectUserLoading);
  const user = useAppSelector(selectUser);
  // fetchUserInfo sẽ lấy lại thông tin user từ server, session và cập nhật vào Redux state.
  // đảm bảo khi f5 trang thì userStateRedux vẫn được lưu trong redux state
  // vì khi f5 trang thì userStateRedux sẽ bị mất, nên cần lấy lại thông tin user từ server, session và cập nhật vào Redux state.
  
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      dispatch(fetchUserInfo()); 
    }
  }, [dispatch]);

  useEffect(() => {
    const checkToken = () => {
      const token = localStorage.getItem('token');
      
      // Kiểm tra token nếu có token (không cần đợi user load)
      if (token && isTokenExpired(token)) {
        // Chỉ dispatch event để hiện modal, không xóa dữ liệu ngay
        window.dispatchEvent(new CustomEvent('tokenExpired', { 
          detail: { message: 'Phiên đăng nhập đã hết hạn' } 
        }));
      }
    };
    
    // Kiểm tra ngay lập tức khi component mount
    checkToken();
    
    // Kiểm tra định kỳ mỗi 5 giây
    const interval = setInterval(checkToken, 5000);
    return () => clearInterval(interval);
  }, []); // Bỏ dependency [user] để chạy ngay khi mount

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-[#cc3333]"></div>
      </div>
    );
  }

  return <>{children}</>;
};

export default AppInitializer; 