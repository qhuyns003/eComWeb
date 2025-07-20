import React, { useState } from 'react';
import { FiMapPin, FiGift, FiChevronDown, FiPhone } from 'react-icons/fi';
import { FaTag } from 'react-icons/fa';

interface OrderShopGroupProps {
  group: any;
  idx: number;
  shopInfo?: any;
  shippingFee?: any;
  coupons?: any[];
}

const OrderShopGroup: React.FC<OrderShopGroupProps> = ({ group, idx, shopInfo, shippingFee, coupons = [] }) => {
  const [showCouponModal, setShowCouponModal] = useState(false);
  const [selectedDiscountCoupon, setSelectedDiscountCoupon] = useState<any>(null);
  const [selectedFreeshipCoupon, setSelectedFreeshipCoupon] = useState<any>(null);

  const actualShippingFee =
    (shippingFee?.data?.service_fee ?? shippingFee?.serviceFee ?? shippingFee?.service_fee ?? shippingFee ?? 0);

  // Tính giảm giá sản phẩm
  const calcProductDiscount = () => {
    if (!selectedDiscountCoupon) return 0;
    const totalProduct = group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0);
    if (selectedDiscountCoupon.discountType === 'AMOUNT') {
      return Math.min(selectedDiscountCoupon.discount, totalProduct);
    } else if (selectedDiscountCoupon.discountType === 'PERCENTAGE') {
      return Math.round(totalProduct * selectedDiscountCoupon.discount / 100);
    }
    return 0;
  };
  const productDiscount = calcProductDiscount();

  // Tính giảm giá vận chuyển
  const calcShippingDiscount = () => {
    if (!selectedFreeshipCoupon) return 0;
    if (selectedFreeshipCoupon.discountType === 'AMOUNT') {
      return Math.min(selectedFreeshipCoupon.discount, actualShippingFee);
    } else if (selectedFreeshipCoupon.discountType === 'PERCENTAGE') {
      return Math.round(actualShippingFee * selectedFreeshipCoupon.discount / 100);
    }
    return 0;
  };
  const shippingDiscount = calcShippingDiscount();
  const finalShippingFee = Math.max(0, actualShippingFee - shippingDiscount);
  const finalAmount = group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0) + finalShippingFee - productDiscount;

  // Phân loại coupon
  const discountCoupons = coupons.filter(c => c.couponType === 'DISCOUNT');
  const freeshipCoupons = coupons.filter(c => c.couponType === 'SHIPPING');

  return (
    <div className="mb-8 border rounded-xl shadow-sm bg-[#faeaea] border-[#f5d5d5]">
      <div className="flex items-center gap-2 px-4 py-3 border-b border-[#f5d5d5]">
        <span className="text-[#cc3333] font-bold text-lg"><FiMapPin className="inline mr-1" />{group.shop.name}</span>
      </div>
      {shopInfo && (
        <div className="px-4 py-2 bg-white border-b border-[#f5d5d5]">
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <FiMapPin className="text-[#cc3333]" />
            <span className="font-medium">Địa chỉ shop:</span>
          </div>
          <div className="text-sm text-gray-800 font-medium mt-1">
            {shopInfo.fullAddress || shopInfo.detailAddress || 'Chưa có thông tin địa chỉ'}
          </div>
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
        <button
          className="flex items-center px-3 py-1 rounded-full border border-[#cc3333] text-[#cc3333] font-semibold text-sm hover:bg-[#f5d5d5]"
          onClick={() => setShowCouponModal(true)}
        >
          <FaTag className="mr-1" />
          {selectedDiscountCoupon ? selectedDiscountCoupon.code : 'Chọn voucher giảm giá'}
          {selectedFreeshipCoupon ? `, ${selectedFreeshipCoupon.code}` : ''}
          <FiChevronDown className="ml-1" />
        </button>
        {productDiscount > 0 && <span className="ml-2 text-green-600 font-bold">- {productDiscount.toLocaleString()}₫</span>}
      </div>
      {/* Modal chọn coupon */}
      {showCouponModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-md relative animate-fadeIn">
            <button className="absolute top-3 right-3 text-gray-400 hover:text-[#cc3333] text-2xl font-bold transition" onClick={() => setShowCouponModal(false)} aria-label="Đóng">×</button>
            <h2 className="text-2xl font-bold mb-4 text-[#cc3333] text-center">Chọn voucher</h2>
            <div className="flex flex-col gap-4 max-h-80 overflow-y-auto">
              <div>
                <div className="font-semibold text-[#cc3333] mb-2">Voucher giảm giá</div>
                <div className="flex flex-col gap-1">
                  <label className={`flex items-center w-full p-3 rounded-lg border ${!selectedDiscountCoupon ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                    <input type="radio" name={`discount-coupon-${group.shop.id}`} checked={!selectedDiscountCoupon} onChange={() => setSelectedDiscountCoupon(null)} className="mr-2" />
                    <span className="font-medium text-gray-700">Không dùng voucher</span>
                  </label>
                  {discountCoupons.length === 0 && <div className="text-gray-500 text-center">Không có voucher giảm giá</div>}
                  {discountCoupons.map((coupon: any) => (
                    <label key={coupon.id} className={`flex flex-col items-start w-full p-3 rounded-lg border ${selectedDiscountCoupon?.id === coupon.id ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                      <input type="radio" name={`discount-coupon-${group.shop.id}`} checked={selectedDiscountCoupon?.id === coupon.id} onChange={() => setSelectedDiscountCoupon(coupon)} className="mr-2" />
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-bold text-[#cc3333]">{coupon.code}</span>
                        <span className="text-xs px-2 py-1 rounded bg-gray-200 text-gray-700 font-semibold">{coupon.discountType === 'AMOUNT' ? 'Số tiền' : 'Phần trăm'}</span>
                      </div>
                      <div className="text-sm text-gray-700">
                        {coupon.discountType === 'AMOUNT'
                          ? `Giảm ${coupon.discount.toLocaleString()}₫ trên đơn tối thiểu ${coupon.minOrder.toLocaleString()}₫`
                          : `Giảm ${coupon.discount}% trên đơn tối thiểu ${coupon.minOrder.toLocaleString()}₫`}
                      </div>
                      <div className="text-xs text-gray-500 mt-1">HSD: {coupon.endDate}</div>
                    </label>
                  ))}
                </div>
              </div>
              <div>
                <div className="font-semibold text-[#cc3333] mb-2">Voucher vận chuyển</div>
                <div className="flex flex-col gap-1">
                  <label className={`flex items-center w-full p-3 rounded-lg border ${!selectedFreeshipCoupon ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                    <input type="radio" name={`freeship-coupon-${group.shop.id}`} checked={!selectedFreeshipCoupon} onChange={() => setSelectedFreeshipCoupon(null)} className="mr-2" />
                    <span className="font-medium text-gray-700">Không dùng voucher</span>
                  </label>
                  {freeshipCoupons.length === 0 && <div className="text-gray-500 text-center">Không có voucher vận chuyển</div>}
                  {freeshipCoupons.map((coupon: any) => (
                    <label key={coupon.id} className={`flex flex-col items-start w-full p-3 rounded-lg border ${selectedFreeshipCoupon?.id === coupon.id ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                      <input type="radio" name={`freeship-coupon-${group.shop.id}`} checked={selectedFreeshipCoupon?.id === coupon.id} onChange={() => setSelectedFreeshipCoupon(coupon)} className="mr-2" />
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-bold text-[#cc3333]">{coupon.code}</span>
                        <span className="text-xs px-2 py-1 rounded bg-gray-200 text-gray-700 font-semibold">Vận chuyển</span>
                      </div>
                      <div className="text-sm text-gray-700">
                        {`Giảm tối đa ${coupon.discount.toLocaleString()}₫ phí vận chuyển`}
                      </div>
                      <div className="text-xs text-gray-500 mt-1">HSD: {coupon.endDate}</div>
                    </label>
                  ))}
                </div>
              </div>
            </div>
            <div className="flex justify-end mt-4 gap-2">
              <button className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold" onClick={() => setShowCouponModal(false)}>Đóng</button>
              <button className="px-4 py-2 rounded bg-[#cc3333] text-white font-bold" onClick={() => setShowCouponModal(false)}>Xác nhận</button>
            </div>
          </div>
        </div>
      )}
      {/* Tổng kết từng shop */}
      <div className="px-4 pb-4">
        <div className="flex justify-between text-sm text-gray-700"><span>Tổng tiền hàng:</span><span>{group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0).toLocaleString()}₫</span></div>
        <div className="flex justify-between text-sm text-gray-700">
          <span>Phí vận chuyển:</span>
          <span>
            {shippingDiscount > 0
              ? <>{actualShippingFee.toLocaleString()}₫<span className="text-green-600 font-bold"> -{shippingDiscount.toLocaleString()}₫</span> = {finalShippingFee.toLocaleString()}₫</>
              : <>{actualShippingFee.toLocaleString()}₫</>
            }
          </span>
        </div>
        <div className="flex justify-between text-sm text-gray-700"><span>Giảm giá shop:</span><span>-{productDiscount.toLocaleString()}₫</span></div>
        <div className="flex justify-between text-base font-bold text-[#cc3333] mt-2"><span>Thành tiền:</span><span>{finalAmount.toLocaleString()}₫</span></div>
      </div>
    </div>
  );
};

export default OrderShopGroup; 