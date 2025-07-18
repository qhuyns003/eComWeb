import React from 'react';
import { FiMapPin, FiGift, FiChevronDown } from 'react-icons/fi';
import { FaTag } from 'react-icons/fa';

interface OrderShopGroupProps {
  group: any;
  idx: number;
}

const OrderShopGroup: React.FC<OrderShopGroupProps> = ({ group, idx }) => {
  return (
    <div className="mb-8 border rounded-xl shadow-sm bg-[#faeaea] border-[#f5d5d5]">
      <div className="flex items-center gap-2 px-4 py-3 border-b border-[#f5d5d5]">
        <span className="text-[#cc3333] font-bold text-lg"><FiMapPin className="inline mr-1" />{group.shop.name}</span>
      </div>
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
      {/* Shipping method */}
      <div className="px-4 py-3 flex items-center gap-2">
        <span className="font-semibold text-gray-700">Giao hàng:</span>
        <div className="flex gap-2">
          {group.shippingMethods.map((method: any) => (
            <button key={method.value} className={`flex items-center px-3 py-1 rounded-full border text-sm font-semibold transition-all duration-300 ${group.selectedShipping === method.value ? 'bg-[#cc3333] text-white border-[#cc3333]' : 'bg-white text-[#cc3333] border-[#cc3333]'} hover:bg-[#f5d5d5]`}>
              {method.icon}{method.label} <span className="ml-1">+{method.fee.toLocaleString()}₫</span>
            </button>
          ))}
        </div>
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
        <div className="flex justify-between text-sm text-gray-700"><span>Phí vận chuyển:</span><span>{group.shippingMethods.find((m: any) => m.value === group.selectedShipping)?.fee.toLocaleString()}₫</span></div>
        <div className="flex justify-between text-sm text-gray-700"><span>Giảm giá shop:</span><span>-{group.shopDiscount.toLocaleString()}₫</span></div>
        <div className="flex justify-between text-base font-bold text-[#cc3333] mt-2"><span>Thành tiền:</span><span>{(group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0) + (group.shippingMethods.find((m: any) => m.value === group.selectedShipping)?.fee || 0) - group.shopDiscount).toLocaleString()}₫</span></div>
      </div>
    </div>
  );
};

export default OrderShopGroup; 