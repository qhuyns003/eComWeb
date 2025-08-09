import React from 'react';


interface ShopActionButtonProps {
  isSeller: boolean;
  onShopInfo?: () => void;
  onRegisterShop?: () => void;
  className?: string;
}

const ShopActionButton: React.FC<ShopActionButtonProps> = ({ isSeller, onShopInfo, onRegisterShop, className = "" }) => {
  const baseClass = "px-5 py-2 rounded-full shadow hover:scale-105 transition-transform font-semibold text-white text-base leading-tight";
  return isSeller ? (
    <button
      type="button"
      onClick={onShopInfo}
      className={`${baseClass} bg-gradient-to-r from-green-500 to-green-600 ${className}`}
    >
      Thông tin shop
    </button>
  ) : (
    <button
      type="button"
      onClick={onRegisterShop}
      className={`${baseClass} bg-gradient-to-r from-yellow-500 to-yellow-600 ${className}`}
    >
      Đăng ký kênh bán
    </button>
  );
};

export default ShopActionButton;
