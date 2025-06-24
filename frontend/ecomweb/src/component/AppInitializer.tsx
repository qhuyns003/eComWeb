import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { fetchUserInfo, selectUserLoading } from '../store/features/userSlice';

const AppInitializer: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const dispatch = useAppDispatch();
  const loading = useAppSelector(selectUserLoading);

  // fetchUserInfo sẽ lấy lại thông tin user từ server, session và cập nhật vào Redux state.
  // đảm bảo khi f5 trang thì userStateRedux vẫn được lưu trong redux state
  // vì khi f5 trang thì userStateRedux sẽ bị mất, nên cần lấy lại thông tin user từ server, session và cập nhật vào Redux state.
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      dispatch(fetchUserInfo()); 
    }
  }, [dispatch]);

  // Có thể hiển thị loading spinner nếu cần
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