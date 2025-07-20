import React from 'react';
import { FiMapPin, FiGift, FiChevronDown, FiPhone } from 'react-icons/fi';
import { FaTag } from 'react-icons/fa';

interface OrderShopGroupProps {
  group: any;
  idx: number;
  shopInfo?: any; // Thêm prop shopInfo từ API
  shippingFee?: any; // Thêm prop shippingFee từ GHN API
}

const OrderShopGroup: React.FC<OrderShopGroupProps> = ({ group, idx, shopInfo, shippingFee }) => {
  // Tính phí ship thực tế từ GHN API hoặc fallback về dữ liệu giả định
  const actualShippingFee = shippingFee?.data?.service_fee || 
    group.shippingMethods.find((m: any) => m.value === group.selectedShipping)?.fee || 0;
  
  // Debug log
  console.log(`[OrderShopGroup ${group.shop.id}] shippingFee:`, shippingFee);
  console.log(`[OrderShopGroup ${group.shop.id}] actualShippingFee:`, actualShippingFee);
  
  return (
    <div className="mb-8 border rounded-xl shadow-sm bg-[#faeaea] border-[#f5d5d5]">
      <div className="flex items-center gap-2 px-4 py-3 border-b border-[#f5d5d5]">
        <span className="text-[#cc3333] font-bold text-lg"><FiMapPin className="inline mr-1" />{group.shop.name}</span>
      </div>
      
      {/* Hiển thị thông tin vị trí shop nếu có */}
      {shopInfo && (
        <div className="px-4 py-2 bg-white border-b border-[#f5d5d5]">
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <FiMapPin className="text-[#cc3333]" />
            <span className="font-medium">Địa chỉ shop:</span>
          </div>
          
          {/* Hiển thị địa chỉ đầy đủ */}
          <div className="text-sm text-gray-800 font-medium mt-1">
            {shopInfo.fullAddress || shopInfo.detailAddress || 'Chưa có thông tin địa chỉ'}
          </div>
          
          {/* Thông tin bổ sung */}
          <div className="flex flex-wrap gap-4 mt-2 text-xs text-gray-500">
            {shopInfo.phoneNumber && (
              <div className="flex items-center gap-1">
                <FiPhone className="text-[#cc3333]" />
                <span>{shopInfo.phoneNumber}</span>
              </div>
            )}
           
            
          </div>
        </div>
      )}
      
      <div className="divide-y divide-[#f5d5d5]">
        {group.products.map((p: any, i: number) => (
          <div key={p.id} className="flex items-center gap-4 px-4 py-3">
            <div className="w-16 h-16 bg-gray-200 rounded-lg flex items-center justify-center overflow-hidden">
              {p.image ? (
                <img src={p.image} alt={p.name} className="object-cover w-full h-full" />
              ) : (
                <span className="text-gray-400 text-2xl">🛒</span>
              )}
            </div>
            <div className="flex-1 min-w-0">
              <div className="font-semibold text-gray-900 truncate">{p.name}</div>
              <div className="text-xs text-gray-500 truncate">{p.attrs.join(', ')}</div>
            </div>
            <div className="text-right">
              <div className="text-[#cc3333] font-bold">{p.price.toLocaleString()}₫</div>
              <div className="text-gray-700 text-sm">x{p.quantity}</div>
            </div>
          </div>
        ))}
      </div>
      {/* Voucher shop */}
      <div className="px-4 pb-3 flex items-center gap-2">
        <span className="font-semibold text-gray-700"><FiGift className="inline mr-1 text-[#cc3333]" />Voucher shop:</span>
        <button className="flex items-center px-3 py-1 rounded-full border border-[#cc3333] text-[#cc3333] font-semibold text-sm hover:bg-[#f5d5d5]">
          <FaTag className="mr-1" />Chọn voucher
          <FiChevronDown className="ml-1" />
        </button>
        {group.shopDiscount > 0 && <span className="ml-2 text-green-600 font-bold">- {group.shopDiscount.toLocaleString()}₫</span>}
      </div>
      {/* Tổng kết từng shop */}
      <div className="px-4 pb-4">
        <div className="flex justify-between text-sm text-gray-700"><span>Tổng tiền hàng:</span><span>{group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0).toLocaleString()}₫</span></div>
        <div className="flex justify-between text-sm text-gray-700"><span>Phí vận chuyển:</span><span>{actualShippingFee.toLocaleString()}₫</span></div>
        <div className="flex justify-between text-sm text-gray-700"><span>Giảm giá shop:</span><span>-{group.shopDiscount.toLocaleString()}₫</span></div>
        <div className="flex justify-between text-base font-bold text-[#cc3333] mt-2"><span>Thành tiền:</span><span>{(group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0) + actualShippingFee - group.shopDiscount).toLocaleString()}₫</span></div>
      </div>
    </div>
  );
};

export default OrderShopGroup; 