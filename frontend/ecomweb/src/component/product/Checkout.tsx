import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { useLocation } from 'react-router-dom';
import { FiCreditCard, FiDollarSign, FiSmartphone, FiTruck, FiGift, FiMapPin, FiUser, FiChevronDown, FiPlus, FiX } from 'react-icons/fi';
import { FaShippingFast, FaMoneyBillWave, FaTag, FaGift } from 'react-icons/fa';
import { getUserAddresses, getProvinces, getDistricts, getWards } from '../../api/api';

const paymentMethods = [
  { value: 'COD', label: 'Thanh toán khi nhận hàng', icon: <FiDollarSign className="inline mr-2 text-xl text-[#cc3333]" /> },
  { value: 'CARD', label: 'Thẻ ngân hàng', icon: <FiCreditCard className="inline mr-2 text-xl text-[#cc3333]" /> },
  { value: 'EWALLET', label: 'Ví điện tử', icon: <FiSmartphone className="inline mr-2 text-xl text-[#cc3333]" /> },
];

const orderVoucherList = [
  { value: 'ORDER10', label: 'Giảm 10k cho đơn từ 200k', icon: <FaTag className="inline mr-2 text-lg text-[#cc3333]" />, discount: 10000 },
  { value: 'ORDER20', label: 'Giảm 20k cho đơn từ 400k', icon: <FaTag className="inline mr-2 text-lg text-[#cc3333]" />, discount: 20000 },
];

const Checkout: React.FC = () => {
  const location = useLocation();
  const [orderShopGroups, setOrderShopGroups] = useState(location.state?.orderShopGroups || []);
  const [userAddresses, setUserAddresses] = useState<any[]>([]);
  const [selectedAddress, setSelectedAddress] = useState<any | null>(null);
  const [showAddAddressModal, setShowAddAddressModal] = useState(false);
  const [newAddress, setNewAddress] = useState({ receiverName: '', phoneNumber: '', detailAddress: '', ward: '', district: '', province: '' });
  const [receiverName, setReceiverName] = useState('');
  const [receiverPhone, setReceiverPhone] = useState('');
  const [receiverAddress, setReceiverAddress] = useState('');
  const [note, setNote] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('COD');
  const [orderVoucher, setOrderVoucher] = useState('');
  const [provinces, setProvinces] = useState<any[]>([]);
  const [districts, setDistricts] = useState<any[]>([]);
  const [wards, setWards] = useState<any[]>([]);

  // Lấy địa chỉ thật khi vào trang
  useEffect(() => {
    getUserAddresses().then(addresses => {
      setUserAddresses(addresses);
      const defaultAddr = addresses.find((addr: any) => addr.default);
      setSelectedAddress(defaultAddr || addresses[0] || null);
    });
  }, []);

  // Khi chọn địa chỉ, tự động fill thông tin người nhận
  useEffect(() => {
    if (selectedAddress) {
      setReceiverName(selectedAddress.receiverName);
      setReceiverPhone(selectedAddress.phoneNumber);
      setReceiverAddress(selectedAddress.fullAddress || `${selectedAddress.detailAddress}, ${selectedAddress.ward}, ${selectedAddress.district}, ${selectedAddress.province}`);
      // Có thể dùng selectedAddress.latitude, longitude ở các bước sau
    }
  }, [selectedAddress]);

  // Load provinces khi mở modal
  useEffect(() => {
    if (showAddAddressModal) {
      getProvinces().then(res => setProvinces(res.data.data || []));
    }
  }, [showAddAddressModal]);

  const handleProvinceChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const provinceId = Number(e.target.value);
    const provinceName = provinces.find((p: any) => p.ProvinceID === provinceId)?.ProvinceName || '';
    setNewAddress(a => ({ ...a, province: provinceName, district: '', ward: '' }));
    getDistricts(provinceId).then(res => setDistricts(res.data.data || []));
    setWards([]);
  };
  const handleDistrictChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const districtId = Number(e.target.value);
    const districtName = districts.find((d: any) => d.DistrictID === districtId)?.DistrictName || '';
    setNewAddress(a => ({ ...a, district: districtName, ward: '' }));
    getWards(districtId).then(res => setWards(res.data.data || []));
  };
  const handleWardChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const wardCode = e.target.value;
    const wardName = wards.find((w: any) => w.WardCode === wardCode)?.WardName || '';
    setNewAddress(a => ({ ...a, ward: wardName }));
  };

  // Tính toán tổng tiền
  const totalProduct = orderShopGroups.reduce((sum: number, group: any) => sum + group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0), 0);
  const totalShipping = orderShopGroups.reduce((sum: number, group: any) => sum + (group.shippingMethods.find((m: any) => m.value === group.selectedShipping)?.fee || 0), 0);
  const totalShopDiscount = orderShopGroups.reduce((sum: number, group: any) => sum + (group.shopDiscount || 0), 0);
  const orderVoucherDiscount = orderVoucherList.find((v: any) => v.value === orderVoucher)?.discount || 0;
  const finalAmount = totalProduct + totalShipping - totalShopDiscount - orderVoucherDiscount;

  const handleConfirm = () => {
    if (!receiverName || !receiverPhone || !receiverAddress) {
      toast.error('Vui lòng nhập đầy đủ thông tin nhận hàng!');
      return;
    }
    toast.success('Đặt hàng thành công!');
  };

  // Thêm địa chỉ mới
  const handleAddAddress = (e: React.FormEvent) => {
    e.preventDefault();
    const id = (Math.random() * 100000).toFixed(0);
    const addr = { ...newAddress, id, default: false };
    setUserAddresses(prev => [...prev, addr]);
    setSelectedAddress(addr);
    setShowAddAddressModal(false);
    setNewAddress({ receiverName: '', phoneNumber: '', detailAddress: '', ward: '', district: '', province: '' });
    toast.success('Thêm địa chỉ thành công!');
  };

  if (!orderShopGroups.length) {
    return <div className="min-h-screen flex items-center justify-center text-red-500 text-xl font-bold">Không có dữ liệu đơn hàng!</div>;
  }

  // UI
  return (
    <div className="min-h-screen bg-gray-100 py-8 px-2 sm:px-6 flex justify-center items-start">
      <div className="w-full max-w-6xl bg-white rounded-2xl shadow-2xl flex flex-col md:flex-row gap-8 p-4 sm:p-8">
        {/* Tiêu đề ở trên cùng */}
        <div className="w-full absolute left-0 top-0 flex justify-center z-10">
          <h1 className="text-3xl sm:text-4xl font-extrabold text-[#cc3333] text-center py-4">Xác nhận và thanh toán</h1>
        </div>
        <div className="w-full h-12 md:hidden" /> {/* spacing cho mobile */}
        {/* Bên trái: Thông tin sản phẩm, shop, shipping, voucher shop */}
        <div className="flex-1 min-w-0 mt-16 md:mt-0">
          {orderShopGroups.map((group: any, idx: number) => (
            <div key={group.shop.id} className="mb-8 border rounded-xl shadow-sm bg-[#faeaea] border-[#f5d5d5]">
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
                    <button key={method.value} className={`flex items-center px-3 py-1 rounded-full border text-sm font-semibold transition-all duration-300 ${group.selectedShipping === method.value ? 'bg-[#cc3333] text-white border-[#cc3333]' : 'bg-white text-[#cc3333] border-[#cc3333]'} hover:bg-[#f5d5d5]`}>{method.icon}{method.label} <span className="ml-1">+{method.fee.toLocaleString()}₫</span></button>
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
          ))}
        </div>
        {/* Bên phải: Địa chỉ, người nhận, payment, voucher toàn đơn, submit */}
        <div className="w-full md:w-[350px] flex-shrink-0 flex flex-col gap-6 mt-16 md:mt-0">
          {/* Chọn địa chỉ nhận hàng */}
          <div className="bg-[#faeaea] border border-[#f5d5d5] rounded-xl p-4 flex flex-col gap-3">
            <div className="flex items-center gap-2 mb-2">
              <FiUser className="text-[#cc3333] text-xl" />
              <span className="font-bold text-[#cc3333] text-lg">Thông tin nhận hàng</span>
            </div>
            <div className="flex flex-col gap-2">
              {userAddresses.map((addr: any) => (
                <label key={addr.id} className={`flex items-start px-3 py-2 rounded-lg border cursor-pointer transition-all ${selectedAddress?.id === addr.id ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333] gap-2`}>
                  <input
                    type="radio"
                    name="userAddress"
                    value={addr.id}
                    checked={selectedAddress?.id === addr.id}
                    onChange={() => setSelectedAddress(addr)}
                    className="mr-2 accent-[#cc3333] mt-1"
                  />
                  <div className="flex flex-col flex-1 min-w-0">
                    <span className="font-semibold text-gray-900">{addr.receiverName}</span>
                    <span className="text-gray-900">{addr.phoneNumber}</span>
                    <span className="text-gray-700 text-sm break-words">{addr.fullAddress || `${addr.detailAddress}, ${addr.ward}, ${addr.district}, ${addr.province}`}</span>
                  </div>
                  {addr.default && <span className="ml-2 px-2 py-1 bg-[#cc3333] text-white text-xs rounded self-start">Mặc định</span>}
                </label>
              ))}
              <button
                className="mt-2 px-3 py-2 rounded-lg border border-dashed border-[#cc3333] text-[#cc3333] font-semibold flex items-center gap-2 hover:bg-[#f5d5d5]"
                onClick={() => setShowAddAddressModal(true)}
              >
                <FiPlus /> Thêm địa chỉ mới
              </button>
            </div>
          </div>
          {/* Modal thêm địa chỉ mới */}
          {showAddAddressModal && (
            <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
              <div className="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-md relative animate-fadeIn">
                <button className="absolute top-3 right-3 text-gray-400 hover:text-[#cc3333] text-2xl font-bold transition" onClick={() => setShowAddAddressModal(false)} aria-label="Đóng"><FiX /></button>
                <h2 className="text-2xl font-bold mb-4 text-[#cc3333] text-center">Thêm địa chỉ mới</h2>
                <form onSubmit={handleAddAddress} className="flex flex-col gap-3">
                  <div>
                    <label className="block font-semibold text-gray-700 mb-1">Họ tên</label>
                    <input type="text" className="border rounded px-3 py-2 w-full" placeholder="Họ tên người nhận" value={newAddress.receiverName} onChange={e => setNewAddress(a => ({ ...a, receiverName: e.target.value }))} required />
                  </div>
                  <div>
                    <label className="block font-semibold text-gray-700 mb-1">Số điện thoại</label>
                    <input type="text" className="border rounded px-3 py-2 w-full" placeholder="Số điện thoại" value={newAddress.phoneNumber} onChange={e => setNewAddress(a => ({ ...a, phoneNumber: e.target.value }))} required />
                  </div>
                  <div>
                    <label className="block font-semibold text-gray-700 mb-1">Tỉnh/Thành phố</label>
                    <select required onChange={handleProvinceChange} value={provinces.find((p: any) => p.ProvinceName === newAddress.province)?.ProvinceID || ''} className="border rounded px-3 py-2 w-full">
                      <option value="">Chọn tỉnh/thành</option>
                      {provinces.map((p: any) => (
                        <option key={p.ProvinceID} value={p.ProvinceID}>{p.ProvinceName}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block font-semibold text-gray-700 mb-1">Quận/Huyện</label>
                    <select required onChange={handleDistrictChange} value={districts.find((d: any) => d.DistrictName === newAddress.district)?.DistrictID || ''} disabled={!districts.length} className="border rounded px-3 py-2 w-full">
                      <option value="">Chọn quận/huyện</option>
                      {districts.map((d: any) => (
                        <option key={d.DistrictID} value={d.DistrictID}>{d.DistrictName}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block font-semibold text-gray-700 mb-1">Phường/Xã</label>
                    <select required onChange={handleWardChange} value={wards.find((w: any) => w.WardName === newAddress.ward)?.WardCode || ''} disabled={!wards.length} className="border rounded px-3 py-2 w-full">
                      <option value="">Chọn phường/xã</option>
                      {wards.map((w: any) => (
                        <option key={w.WardCode} value={w.WardCode}>{w.WardName}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block font-semibold text-gray-700 mb-1">Địa chỉ cụ thể</label>
                    <input type="text" className="border rounded px-3 py-2 w-full" placeholder="Số nhà, tên đường, tòa nhà..." value={newAddress.detailAddress} onChange={e => setNewAddress(a => ({ ...a, detailAddress: e.target.value }))} required />
                  </div>
                  <button type="submit" className="w-full mt-2 py-2 rounded-full bg-[#cc3333] text-white font-bold text-lg shadow-lg hover:bg-[#b82d2d] transition">Lưu địa chỉ</button>
                </form>
              </div>
            </div>
          )}
          <div className="bg-[#faeaea] border border-[#f5d5d5] rounded-xl p-4 flex flex-col gap-3">
            <div className="flex items-center gap-2 mb-2">
              <FiCreditCard className="text-[#cc3333] text-xl" />
              <span className="font-bold text-[#cc3333] text-lg">Phương thức thanh toán</span>
            </div>
            <div className="flex flex-col gap-2">
              {paymentMethods.map(method => (
                <label key={method.value} className={`flex items-center px-3 py-2 rounded-lg border cursor-pointer transition-all ${paymentMethod === method.value ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                  <input
                    type="radio"
                    name="paymentMethod"
                    value={method.value}
                    checked={paymentMethod === method.value}
                    onChange={() => setPaymentMethod(method.value)}
                    className="mr-2 accent-[#cc3333]"
                  />
                  {method.icon}
                  <span className="font-medium text-gray-900">{method.label}</span>
                </label>
              ))}
            </div>
          </div>
          <div className="bg-[#faeaea] border border-[#f5d5d5] rounded-xl p-4 flex flex-col gap-3">
            <div className="flex items-center gap-2 mb-2">
              <FaGift className="text-[#cc3333] text-xl" />
              <span className="font-bold text-[#cc3333] text-lg">Voucher toàn đơn</span>
            </div>
            <div className="flex flex-col gap-2">
              {orderVoucherList.map(v => (
                <label key={v.value} className={`flex items-center px-3 py-2 rounded-lg border cursor-pointer transition-all ${orderVoucher === v.value ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                  <input
                    type="radio"
                    name="orderVoucher"
                    value={v.value}
                    checked={orderVoucher === v.value}
                    onChange={() => setOrderVoucher(v.value)}
                    className="mr-2 accent-[#cc3333]"
                  />
                  {v.icon}
                  <span className="font-medium text-gray-900">{v.label}</span>
                  <span className="ml-auto text-green-600 font-bold">-{v.discount.toLocaleString()}₫</span>
                </label>
              ))}
            </div>
          </div>
          <div className="bg-white border border-[#f5d5d5] rounded-xl p-4 flex flex-col gap-2 shadow">
            <div className="flex justify-between text-base font-semibold text-gray-700"><span>Tổng tiền hàng:</span><span>{totalProduct.toLocaleString()}₫</span></div>
            <div className="flex justify-between text-base font-semibold text-gray-700"><span>Tổng phí ship:</span><span>{totalShipping.toLocaleString()}₫</span></div>
            <div className="flex justify-between text-base font-semibold text-gray-700"><span>Tổng giảm giá:</span><span>-{(totalShopDiscount + orderVoucherDiscount).toLocaleString()}₫</span></div>
            <div className="flex justify-between text-xl font-extrabold text-[#cc3333] mt-2"><span>Thành tiền:</span><span>{finalAmount.toLocaleString()}₫</span></div>
            <button
              className="w-full mt-2 py-3 rounded-full bg-[#cc3333] text-white font-bold text-lg shadow-lg hover:bg-[#b82d2d] transition disabled:opacity-50 disabled:cursor-not-allowed"
              onClick={handleConfirm}
              disabled={!receiverName || !receiverPhone || !receiverAddress}
            >
              Đặt hàng ngay
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout; 