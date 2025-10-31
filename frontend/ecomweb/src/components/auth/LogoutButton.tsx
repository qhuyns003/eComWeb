import React from 'react';
import { useAuth } from '../../contexts/AuthContext';

const LogoutButton: React.FC = () => {
  const { logout, user } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="flex items-center gap-3">
      {user && (
        <span className="text-sm text-gray-600">
          Xin chào, {user.preferred_username || user.name || 'User'}
        </span>
      )}
      <button
        onClick={handleLogout}
        className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
      >
        Đăng xuất
      </button>
    </div>
  );
};

export default LogoutButton;