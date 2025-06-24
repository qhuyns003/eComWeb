import React from "react";
import { useNavigate } from "react-router-dom";
import { useAppSelector } from "../../store/hooks";
import { selectIsAuthenticated } from "../../store/features/userSlice";

interface ProductCardProps {
  image: string;
  alt: string;
  name: string;
  price: string;
  buyNowLabel?: string;
  productId: number;
}

const ProductCard: React.FC<ProductCardProps> = ({ image, alt, name, price, buyNowLabel = "Mua ngay", productId }) => {
  const navigate = useNavigate();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);

  const handleBuyNow = () => {
    if (isAuthenticated) {
      navigate(`/product/${productId}`);
    } else {
      navigate('/login');
    }
  };

  return (
    <div className="bg-white rounded-xl shadow p-4 flex flex-col items-center hover:shadow-lg transition">
      <img
        src={image}
        alt={alt}
        className="w-32 h-32 object-cover rounded mb-3"
      />
      <div className="font-semibold mb-1 text-center">{name}</div>
      <div className="text-[#cc3333] font-bold mb-2">{price}</div>
      <button 
        onClick={handleBuyNow}
        className="bg-[#cc3333] text-white px-4 py-2 rounded-full hover:bg-[#b82d2d] transition text-sm"
      >
        {buyNowLabel}
      </button>
    </div>
  );
};

export default ProductCard; 