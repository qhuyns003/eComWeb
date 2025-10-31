import React from 'react';

const LoadingSpinner: React.FC = () => {
  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#cc3333] mx-auto mb-4"></div>
        <h2 className="text-xl font-semibold text-gray-700 mb-2">Đang khởi tạo...</h2>
        <p className="text-gray-500">Vui lòng đợi trong giây lát</p>
      </div>
    </div>
  );
};

export default LoadingSpinner;