import React from "react";
import { useNavigate } from "react-router-dom";
import { useAppSelector } from "../../store/hooks";
import { selectIsAuthenticated } from "../../store/features/userSlice";

interface ProductImageResponse {
  id: string;
  url: string;
  isMain: boolean;
}

interface ProductCardProps {
  id: string;
  name: string;
  price: number;
  images: ProductImageResponse[];
  rating: number;
  numberOfOrder: number;
  buyNowLabel?: string;
}

const ProductCard: React.FC<ProductCardProps> = ({ 
  id, 
  name, 
  price, 
  images, 
  rating, 
  numberOfOrder, 
  buyNowLabel = "Mua ngay" 
}) => {
  const navigate = useNavigate();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);

  const handleBuyNow = () => {
    if (isAuthenticated) {
      navigate(`/product/${id}`);
    } else {
      navigate('/login');
    }
  };

  // Lấy ảnh chính (isMain = true) hoặc ảnh đầu tiên nếu không có ảnh chính
  const getMainImage = () => {
    if (!images || images.length === 0) {
      return '/placeholder-image.jpg';
    }
    
    // Tìm ảnh có isMain = true
    const mainImage = images.find(img => img.isMain);
    if (mainImage) {
      return mainImage.url;
    }
    
    // Nếu không có ảnh chính, lấy ảnh đầu tiên
    return images[0].url;
  };

  const imageUrl = getMainImage();

  // Hiển thị rating bằng sao
  const renderStars = (rating: number) => {
    const stars = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;

    for (let i = 0; i < fullStars; i++) {
      stars.push(
        <svg key={i} className="w-4 h-4 text-yellow-400 fill-current" viewBox="0 0 20 20">
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
      );
    }

    if (hasHalfStar) {
      stars.push(
        <svg key="half" className="w-4 h-4 text-yellow-400 fill-current" viewBox="0 0 20 20">
          <defs>
            <linearGradient id="halfStar">
              <stop offset="50%" stopColor="#fbbf24" />
              <stop offset="50%" stopColor="#e5e7eb" />
            </linearGradient>
          </defs>
          <path fill="url(#halfStar)" d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
      );
    }

    // Thêm sao rỗng cho đủ 5 sao
    const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
    for (let i = 0; i < emptyStars; i++) {
      stars.push(
        <svg key={`empty-${i}`} className="w-4 h-4 text-gray-300 fill-current" viewBox="0 0 20 20">
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
      );
    }

    return stars;
  };

  return (
    <div className="bg-white rounded-xl shadow p-4 flex flex-col items-center hover:shadow-lg transition">
      <img
        src={imageUrl}
        alt={name}
        className="w-32 h-32 object-cover rounded mb-3"
      />
      <div className="font-semibold mb-1 text-center text-sm line-clamp-2">{name}</div>
      
      {/* Rating và số lượt mua */}
      <div className="flex items-center gap-1 mb-1">
        {renderStars(rating)}
        <span className="text-xs text-gray-600 ml-1">({Number(rating).toFixed(1)})</span>
      </div>
      
      <div className="text-xs text-gray-500 mb-2">
        Đã bán {typeof numberOfOrder === 'number' && !isNaN(numberOfOrder) ? numberOfOrder.toLocaleString() : 0}
      </div>
      
      <div className="text-[#cc3333] font-bold mb-2">
        {typeof price === 'number' && !isNaN(price) ? price.toLocaleString() : 0}₫
      </div>
      
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