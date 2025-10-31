import React from 'react';
import { useAuth } from '../contexts/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireRole?: string;
  requireSeller?: boolean;
  requireAdmin?: boolean;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requireRole, 
  requireSeller = false, 
  requireAdmin = false 
}) => {
  const { isAuthenticated, hasRole, isSeller, isAdmin } = useAuth();

  if (!isAuthenticated) {
    return (
      <div className="text-center p-8">
        <h2 className="text-xl font-bold text-red-600">Yêu cầu đăng nhập</h2>
        <p>Bạn cần đăng nhập để truy cập trang này.</p>
      </div>
    );
  }

  if (requireAdmin && !isAdmin) {
    return (
      <div className="text-center p-8">
        <h2 className="text-xl font-bold text-red-600">Không có quyền truy cập</h2>
        <p>Bạn cần quyền ADMIN để truy cập trang này.</p>
      </div>
    );
  }

  if (requireSeller && !isSeller) {
    return (
      <div className="text-center p-8">
        <h2 className="text-xl font-bold text-red-600">Không có quyền truy cập</h2>
        <p>Bạn cần quyền SELLER để truy cập trang này.</p>
      </div>
    );
  }

  if (requireRole && !hasRole(requireRole)) {
    return (
      <div className="text-center p-8">
        <h2 className="text-xl font-bold text-red-600">Không có quyền truy cập</h2>
        <p>Bạn cần quyền {requireRole} để truy cập trang này.</p>
      </div>
    );
  }

  return <>{children}</>;
};

export default ProtectedRoute;