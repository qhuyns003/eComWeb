import React, { useState, useEffect, useMemo, useRef } from 'react';
import { toast } from 'react-toastify';
import { useLocation } from 'react-router-dom';
import { FiCreditCard, FiDollarSign, FiSmartphone, FiTruck, FiGift, FiMapPin, FiUser, FiChevronDown, FiPlus, FiX } from 'react-icons/fi';
import { FaShippingFast, FaMoneyBillWave, FaTag, FaGift } from 'react-icons/fa';
import { getUserAddresses, getProvinces, getDistricts, getWards, addUserAddress, getGhnServiceForOrderGroup, calculateShippingFee, getShopInfo, getCouponsByShopId, getUserOrderCoupons, createOrder, getPaymentMethods, createPayment } from '../../api/api';
import OrderShopGroup from './OrderShopGroup';
import { useAppSelector, useAppDispatch } from '../../store/hooks';
import { clearOrder } from '../../store/features/orderSlice';

// XÓA mảng paymentMethods cứng
// const paymentMethods = [
//   { value: 'COD', label: 'Thanh toán khi nhận hàng', icon: <FiDollarSign className="inline mr-2 text-xl text-[#cc3333]" /> },
//   { value: 'BANKING', label: 'Chuyển khoản ngân hàng', icon: <FiCreditCard className="inline mr-2 text-xl text-[#cc3333]" /> },
// ];

const orderVoucherList = [
  { value: 'ORDER10', label: 'Giảm 10k cho đơn từ 200k', icon: <FaTag className="inline mr-2 text-lg text-[#cc3333]" />, discount: 10000 },
  { value: 'ORDER20', label: 'Giảm 20k cho đơn từ 400k', icon: <FaTag className="inline mr-2 text-lg text-[#cc3333]" />, discount: 20000 },
];

const Checkout: React.FC = () => {
  const location = useLocation();
  const orderShopGroups = useAppSelector(state => state.order.orderShopGroups);
  const dispatch = useAppDispatch();
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
  const [shippingInfo, setShippingInfo] = useState<any[]>([]);
  const [shippingFeeResult, setShippingFeeResult] = useState<any[]>([]);
  const [shopInfos, setShopInfos] = useState<any[]>([]);
  const [shopCoupons, setShopCoupons] = useState<{ [shopId: string]: any[] }>({});
  const [orderCoupons, setOrderCoupons] = useState<any[]>([]);
  const [selectedOrderCoupon, setSelectedOrderCoupon] = useState<any>(null);
  const [showOrderCouponModal, setShowOrderCouponModal] = useState(false);
  const [groupDiscounts, setGroupDiscounts] = useState<{ [groupId: string]: { productDiscount: number, shippingDiscount: number } }>({});
  const [selectedShopCoupons, setSelectedShopCoupons] = useState<{ [shopId: string]: any[] }>({});
  const [shopCalculations, setShopCalculations] = useState<{ [shopId: string]: { subtotal: number, shippingFee: number, totalDiscount: number, total: number } }>({});

  // Phân loại voucher toàn đơn
  const orderDiscountCoupons = orderCoupons.filter(c => c.couponType === 'DISCOUNT');
  const orderShippingCoupons = orderCoupons.filter(c => c.couponType === 'SHIPPING');
  const [selectedOrderDiscountCoupon, setSelectedOrderDiscountCoupon] = useState<any>(null);
  const [selectedOrderShippingCoupon, setSelectedOrderShippingCoupon] = useState<any>(null);

  // Trong component Checkout:
  const [paymentMethods, setPaymentMethods] = useState<any[]>([]);

  // Lấy địa chỉ thật khi vào trang
  useEffect(() => {
    getUserAddresses().then(addresses => {
      setUserAddresses(addresses);
      const defaultAddr = addresses.find((addr: any) => addr.defaultAddress);
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

  // Lấy thông tin shop khi vào trang
  useEffect(() => {
    // Lấy danh sách shopId từ orderShopGroups
    const shopIds = orderShopGroups.map((group: any) => group.shop.id);
    if (shopIds.length) {
      getShopInfo(shopIds).then(res => {
        console.log('Shop infos:', res.data.result);
        setShopInfos(res.data.result);
      });
    }
  }, [orderShopGroups]);

  useEffect(() => {
    if (!selectedAddress || !orderShopGroups.length || !shopInfos.length) return;
    const payload = orderShopGroups.map((group: any) => {
      const shopInfo = shopInfos.find((s: any) => String(s.id) === String(group.shop.id));
      return {
        shopId: group.shop.id,
        fromDistrictId: shopInfo?.districtId, // Lấy từ shopInfo
        toDistrictId: selectedAddress.districtId,
      };
    });
    getGhnServiceForOrderGroup(payload)
      .then(res => {
        setShippingInfo(res.data.result);
      })
      .catch(() => {
        setShippingInfo([]);
      });
  }, [selectedAddress, orderShopGroups, shopInfos]);

  // Lấy coupon cho từng shop khi có shopInfos
  useEffect(() => {
    if (!shopInfos.length) return;
    shopInfos.forEach((shop: any) => {
      getCouponsByShopId(shop.id)
        .then(res => {
          setShopCoupons(prev => ({ ...prev, [shop.id]: res.data.result || [] }));
        });
    });
  }, [shopInfos]);

  useEffect(() => {
    getUserOrderCoupons().then(res => {
      setOrderCoupons(res.data.result || []);
    });
  }, []);

  useEffect(() => {
    getPaymentMethods().then(res => {
      setPaymentMethods(res.data.result || []);
    });
  }, []);

  // Hàm tính tổng khối lượng cho 1 group
  const calcTotalWeight = (group: any) => {
    group.products.forEach((p: any, idx: number) => {
      console.log(`[Shop ${group.shop.id}] Product ${idx}: name=${p.name}, weight=${p.weight}, quantity=${p.quantity}`);
    });
    const totalWeight = group.products.reduce((sum: number, p: any) => {
      const productWeight = Number(p.weight) || 150; // Mặc định 100g nếu không có weight
      return sum + productWeight * (p.quantity || 1);
    }, 0);
    console.log(`[Shop ${group.shop.id}] => Total weight: ${totalWeight}`);
    return totalWeight;
  };

  // Mapping serviceId phù hợp cho từng orderGroup
  const orderGroupsWithServiceId = useMemo(() => {
    return orderShopGroups.map((group: any) => {
      const totalWeight = calcTotalWeight(group);
      const info = shippingInfo.find((s: any) => String(s.shopId) === String(group.shop.id));
      let serviceId = null;
      let serviceTypeId = 2;
      if (info && Array.isArray(info.data)) {
        if (totalWeight >= 20000) {
          // Hàng nặng
          const heavy = info.data.find((d: any) => d.service_type_id === 5);
          serviceId = heavy?.service_id;
          serviceTypeId = 5;
        } else {
          // Hàng nhẹ
          const light = info.data.find((d: any) => d.service_type_id === 2);
          serviceId = light?.service_id;
          serviceTypeId = 2;
        }
      }
      return {
        ...group,
        serviceId,
        serviceTypeId,
        totalWeight,
      };
    });
  }, [orderShopGroups, shippingInfo]);

  // Ref để lưu payload cuối cùng
  const lastPayloadRef = useRef<string>("");

  // Khi đã có serviceId, tự động gọi BE để tính phí ship
  useEffect(() => {
    if (!orderGroupsWithServiceId.length || orderGroupsWithServiceId.some(g => !g.serviceId)) return;
    const payloadForFee = orderGroupsWithServiceId.map(group => {
      const { length, width, height } = calcTotalDimensions(group);
      const shopInfo = shopInfos.find((s: any) => String(s.id) === String(group.shop.id));
      const fromDistrictId = shopInfo?.districtId;
      const fromWardCode = shopInfo?.wardCode;
      const toDistrictId = selectedAddress.districtId;
      const toWardCode = selectedAddress.wardCode;
      const insuranceValue = 0;
      const coupon = "";
      if (group.serviceTypeId === 5) {
        // Hàng nặng: truyền items
        const items = group.products.map((p: any) => ({
          name: p.name,
          quantity: p.quantity,
          length: Number(p.length) || 0,
          width: Number(p.width) || 0,
          height: Number(p.height) || 0,
          weight: Number(p.weight) || 0,
        }));
        return {
          shopId: group.shop.id,
          serviceTypeId: group.serviceTypeId,
          fromDistrictId,
          fromWardCode,
          toDistrictId,
          toWardCode,
          insuranceValue,
          coupon,
          items
        };
      } else {
        // Hàng nhẹ: truyền weight, length, width, height
        return {
          shopId: group.shop.id,
          serviceTypeId: group.serviceTypeId,
          fromDistrictId,
          fromWardCode,
          toDistrictId,
          toWardCode,
          weight: group.totalWeight,
          length,
          width,
          height,
          insuranceValue,
          coupon
        };
      }
    });
    const payloadString = JSON.stringify(payloadForFee);
    if (lastPayloadRef.current === payloadString) return;
    lastPayloadRef.current = payloadString;
    console.log('Shipping fee payload:', payloadForFee);
    calculateShippingFee(payloadForFee)
      .then(res => {
        setShippingFeeResult(res.data);
        console.log('Shipping fee result:', res.data);
      })
      .catch((err) => {
        console.error('Shipping fee error:', err);
        setShippingFeeResult([]);
      });
  }, [orderGroupsWithServiceId, selectedAddress, shopInfos]);

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

  // Hàm tính tổng kích thước (ví dụ, có thể cộng dồn hoặc lấy max tuỳ quy tắc đóng gói)
  const calcTotalDimensions = (group: any) => {
    let length = 0, width = 0, height = 0;
    group.products.forEach((p: any) => {
      length = Math.max(length, Number(p.length) || 0);
      width = Math.max(width, Number(p.width) || 0);
      height += (Number(p.height) || 0) * (p.quantity || 1);
    });
    return { length, width, height };
  };

  // Tính toán tổng tiền
  const totalProduct = orderShopGroups.reduce((sum: number, group: any) => sum + group.products.reduce((s: number, p: any) => s + p.price * p.quantity, 0), 0);
  
  // Tính phí ship từ GHN API response
  const totalShipping = useMemo(() => {
    if (!shippingFeeResult) return 0;
    
    // Xử lý response mới (không có result array)
    if (!Array.isArray(shippingFeeResult) && (shippingFeeResult as any).code === 200) {
      return (shippingFeeResult as any).data?.service_fee || 0;
    }
    
    // Xử lý response cũ (có result array)
    const shippingFeeArray = Array.isArray(shippingFeeResult) ? shippingFeeResult : 
      ((shippingFeeResult as any)?.result && Array.isArray((shippingFeeResult as any).result)) ? (shippingFeeResult as any).result : [];
    
    if (!Array.isArray(shippingFeeArray)) return 0;
    
    return shippingFeeArray.reduce((sum: number, result: any) => {
      if (result.code === 200 && result.data) {
        return sum + (result.data.service_fee || 0);
      }
      return sum;
    }, 0);
  }, [shippingFeeResult]);
  
  // Tổng giảm giá các group
  const orderDiscountVoucherDiscount = selectedOrderDiscountCoupon
    ? (selectedOrderDiscountCoupon.discountType === 'AMOUNT'
        ? selectedOrderDiscountCoupon.discount
        : Math.round(totalProduct * selectedOrderDiscountCoupon.discount / 100))
    : 0;
  const orderShippingVoucherDiscount = selectedOrderShippingCoupon
    ? (selectedOrderShippingCoupon.discountType === 'AMOUNT'
        ? Math.min(selectedOrderShippingCoupon.discount, totalShipping)
        : Math.round(totalShipping * selectedOrderShippingCoupon.discount / 100))
    : 0;
  const handleGroupDiscountChange = (productDiscount: number, shippingDiscount: number, groupId: string) => {
    setGroupDiscounts(prev => ({
      ...prev,
      [groupId]: { productDiscount, shippingDiscount }
    }));
  };

  const handleShopCouponChange = (shopId: string, selectedCoupons: any[]) => {
    setSelectedShopCoupons(prev => ({
      ...prev,
      [shopId]: selectedCoupons
    }));
  };

  const handleShopCalculationChange = (shopId: string, calculation: { subtotal: number, shippingFee: number, totalDiscount: number, total: number }) => {
    setShopCalculations(prev => ({
      ...prev,
      [shopId]: calculation
    }));
  };
  // Tổng giảm giá các group (cộng dồn từ state groupDiscounts)
  const totalGroupDiscount = Object.values(groupDiscounts).reduce((sum, d) => sum + (d.productDiscount || 0) + (d.shippingDiscount || 0), 0);
  const totalDiscount = totalGroupDiscount + orderDiscountVoucherDiscount + orderShippingVoucherDiscount;
  const finalAmount = totalProduct + totalShipping - totalDiscount;

  const handleConfirm = async () => {
    if (!receiverName || !receiverPhone || !receiverAddress) {
      toast.error('Vui lòng nhập đầy đủ thông tin nhận hàng!');
      return;
    }
    
    // Đóng gói dữ liệu gửi về BE để tạo đơn hàng
    const payload = {
      // Order fields
      userAddressId: selectedAddress.id,
      payment: paymentMethod, // Payment enum
      total: finalAmount,
      subtotal: totalProduct,
      shippingFee: totalShipping,
      totalDiscount: totalDiscount,
      
      // OrderShopGroups
      orderShopGroups: orderShopGroups.map((group: any) => {
        // Lấy thông tin tính toán từ OrderShopGroup component
        const calculation = shopCalculations[group.shop.id] || {
          subtotal: 0,
          shippingFee: 0,
          totalDiscount: 0,
          total: 0
        };
        
        
        // Lấy coupon IDs đã chọn cho shop này
        const selectedCouponIds = (selectedShopCoupons[group.shop.id] || [])
          .map((coupon: any) => coupon.id);
        
        return {
          shopId: group.shop.id,
          total: calculation.total,
          subTotal: calculation.subtotal,
          shippingFee: calculation.shippingFee,
          totalDiscount: calculation.totalDiscount,
          
          // OrderItems
          orderItems: group.products.map((p: any) => ({
            productVariantId: p.id, // ProductVariant ID
            quantity: p.quantity,
            price: p.price
          })),
          
          // Coupons cho shop này (đã chọn)
          shopCouponIds: selectedCouponIds
        };
      }),
      
      // Coupons cho toàn đơn
      couponIds: [
        selectedOrderDiscountCoupon?.id,
        selectedOrderShippingCoupon?.id
      ].filter(Boolean)
    };
    
    console.log('Order payload:', payload);
    try {
      const response = await createOrder(payload);
      const responseData = response.data;
      if (responseData && responseData.code === 1000) {
        // Nếu thanh toán bằng BANKING, tạo payment URL và chuyển hướng
        if (paymentMethod === 'BANKING') {
          try {
            const orderId = responseData.result?.id; // Lấy order ID từ response
            const paymentPayload = {
              orderId: orderId,
              amount: finalAmount,
              orderInfo: `Thanh toan don hang ${orderId}`
            };
            
            const paymentResponse = await createPayment(paymentPayload);
            const paymentData = paymentResponse.data;
            
            if (paymentData && paymentData.result?.paymentUrl) {
              // Chuyển hướng đến trang thanh toán VNPAY
              window.location.href = paymentData.result.paymentUrl;
            } else {
              toast.error('Không thể tạo URL thanh toán.');
            }
          } catch (paymentError) {
            console.error('Create payment error:', paymentError);
            toast.error('Lỗi khi tạo thanh toán.');
          }
        } else {
          // COD - hiển thị thông báo thành công
          toast.success('Đặt hàng thành công!', {
            style: {
              backgroundColor: '#4ade80',
              color: 'white',
              fontWeight: 'bold'
            }
          });
          dispatch(clearOrder());
        }
      } else {
        toast.error(responseData?.message || 'Đặt hàng thất bại.');
      }
    } catch (error) {
      console.error('Create order error:', error);
      toast.error('Đặt hàng thất bại do lỗi kết nối.');
    }
  };

  // Thêm địa chỉ mới
  const handleAddAddress = async (e: React.FormEvent) => {
    e.preventDefault();
    // Gói dữ liệu gửi về API
    const addressData = {
      receiverName: newAddress.receiverName,
      phoneNumber: newAddress.phoneNumber,
      province: newAddress.province,
      provinceId: provinces.find((p: any) => p.ProvinceName === newAddress.province)?.ProvinceID,
      district: newAddress.district,
      districtId: districts.find((d: any) => d.DistrictName === newAddress.district)?.DistrictID,
      ward: newAddress.ward,
      wardCode: wards.find((w: any) => w.WardName === newAddress.ward)?.WardCode,
      detailAddress: newAddress.detailAddress,
      fullAddress: `${newAddress.detailAddress}, ${newAddress.ward}, ${newAddress.district}, ${newAddress.province}`,
      latitude: null, // Có thể lấy từ API bản đồ nếu cần
      longitude: null,
      defaultAddress: false
    };
    await addUserAddress(addressData);
    // Sau khi lưu thành công, reload lại danh sách địa chỉ và chọn địa chỉ vừa thêm
    getUserAddresses().then(addresses => {
      setUserAddresses(addresses);
      const lastAddr = addresses[addresses.length - 1];
      setSelectedAddress(lastAddr);
    });
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
          {orderShopGroups.map((group: any, idx: number) => {
            const shopInfo = shopInfos.find((s: any) => String(s.id) === String(group.shop.id));
            // Xử lý shippingFeeResult có thể là object hoặc array
            const shippingFeeArray = Array.isArray(shippingFeeResult) ? shippingFeeResult : 
              ((shippingFeeResult as any)?.result && Array.isArray((shippingFeeResult as any).result)) ? (shippingFeeResult as any).result : [];
            // Lấy phí ship cho shop (chỉ lấy phần tử đầu tiên)
            const shippingFeeForShop = shippingFeeArray[0];
            const coupons = shopCoupons[group.shop.id] || [];
            return (
              <OrderShopGroup
                key={group.shop.id}
                group={group}
                idx={idx}
                shopInfo={shopInfo}
                shippingFee={shippingFeeForShop}
                coupons={coupons}
                onDiscountChange={handleGroupDiscountChange}
                onCouponChange={handleShopCouponChange}
                onCalculationChange={handleShopCalculationChange}
              />
            );
          })}
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
                  {(addr.defaultAddress === true ) && <span className="ml-2 px-2 py-1 bg-[#cc3333] text-white text-xs rounded self-start">Mặc định</span>}
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
                <label key={method.name} className={`flex items-center px-3 py-2 rounded-lg border cursor-pointer transition-all ${paymentMethod === method.name ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                  <input
                    type="radio"
                    name="paymentMethod"
                    value={method.name}
                    checked={paymentMethod === method.name}
                    onChange={() => setPaymentMethod(method.name)}
                    className="mr-2 accent-[#cc3333]"
                  />
                  {/* Nếu muốn có icon, có thể map theo method.name */}
                  <span className="font-medium text-gray-900">{method.description}</span>
                </label>
              ))}
            </div>
          </div>
          {/* Voucher toàn đơn */}
          <div className="flex items-center gap-2 mb-2">
            <FaGift className="text-[#cc3333] text-xl" />
            <span className="font-bold text-[#cc3333] text-lg">Voucher toàn đơn</span>
            <button
              className="ml-2 px-3 py-1 rounded-full border border-[#cc3333] text-[#cc3333] font-semibold text-sm hover:bg-[#f5d5d5]"
              onClick={() => setShowOrderCouponModal(true)}
            >
              {selectedOrderDiscountCoupon || selectedOrderShippingCoupon ? 'Đã chọn' : 'Chọn voucher'}
              <FiChevronDown className="ml-1" />
            </button>
          </div>
          {/* Hiển thị voucher đã chọn */}
          {(selectedOrderDiscountCoupon || selectedOrderShippingCoupon) && (
            <div className="flex flex-col gap-1 mb-2 ml-8">
              {selectedOrderDiscountCoupon && (
                <div className="flex items-center gap-2 text-sm text-green-700">
                  <span className="font-bold">Mã giảm giá:</span>
                  <span className="font-semibold">{selectedOrderDiscountCoupon.code}</span>
                  <span>
                    {selectedOrderDiscountCoupon.discountType === 'AMOUNT'
                      ? `- ${selectedOrderDiscountCoupon.discount.toLocaleString()}₫`
                      : `- ${selectedOrderDiscountCoupon.discount}%`}
                  </span>
                </div>
              )}
              {selectedOrderShippingCoupon && (
                <div className="flex items-center gap-2 text-sm text-blue-700">
                  <span className="font-bold">Mã vận chuyển:</span>
                  <span className="font-semibold">{selectedOrderShippingCoupon.code}</span>
                  <span>
                    {selectedOrderShippingCoupon.discountType === 'AMOUNT'
                      ? `- ${selectedOrderShippingCoupon.discount.toLocaleString()}₫`
                      : `- ${selectedOrderShippingCoupon.discount}%`}
                  </span>
                </div>
              )}
            </div>
          )}
          {showOrderCouponModal && (
            <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
              <div className="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-md relative animate-fadeIn">
                <button className="absolute top-3 right-3 text-gray-400 hover:text-[#cc3333] text-2xl font-bold transition" onClick={() => setShowOrderCouponModal(false)} aria-label="Đóng">×</button>
                <h2 className="text-2xl font-bold mb-4 text-[#cc3333] text-center">Chọn voucher toàn đơn</h2>
                <div className="flex flex-col gap-4 max-h-80 overflow-y-auto">
                  {/* Mã giảm giá */}
                  <div>
                    <div className="font-semibold text-[#cc3333] mb-2">Mã giảm giá</div>
                    <div className="flex flex-col gap-1">
                      <label className={`flex items-center w-full p-3 rounded-lg border ${!selectedOrderDiscountCoupon ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                        <input
                          type="radio"
                          name="order-discount-coupon"
                          checked={!selectedOrderDiscountCoupon}
                          onChange={() => setSelectedOrderDiscountCoupon(null)}
                          className="mr-2 accent-[#cc3333]"
                        />
                        <span className="font-medium text-gray-700">Không dùng mã giảm giá</span>
                      </label>
                      {orderDiscountCoupons.length === 0 && <div className="text-gray-500 text-center">Không có mã giảm giá</div>}
                      {orderDiscountCoupons.map((v: any) => (
                        <label
                          key={v.id}
                          className={`flex flex-col items-start w-full p-3 rounded-lg border ${selectedOrderDiscountCoupon?.id === v.id ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}
                        >
                          <input
                            type="radio"
                            name="order-discount-coupon"
                            checked={selectedOrderDiscountCoupon?.id === v.id}
                            onChange={() => setSelectedOrderDiscountCoupon(v)}
                            className="mr-2"
                          />
                          <div className="flex items-center gap-2 mb-1">
                            <span className="font-bold text-[#cc3333]">{v.code}</span>
                            <span className="text-xs px-2 py-1 rounded bg-gray-200 text-gray-700 font-semibold">
                              {v.discountType === 'AMOUNT' ? 'Số tiền' : 'Phần trăm'}
                            </span>
                          </div>
                          <div className="text-sm text-gray-700">
                            {v.discountType === 'AMOUNT'
                              ? `Giảm ${v.discount.toLocaleString()}₫ cho đơn tối thiểu ${v.minOrder.toLocaleString()}₫`
                              : `Giảm ${v.discount}% cho đơn tối thiểu ${v.minOrder.toLocaleString()}₫`}
                          </div>
                          <div className="text-xs text-gray-500 mt-1">HSD: {v.endDate}</div>
                        </label>
                      ))}
                    </div>
                  </div>
                  {/* Voucher vận chuyển */}
                  <div>
                    <div className="font-semibold text-[#cc3333] mb-2">Voucher vận chuyển</div>
                    <div className="flex flex-col gap-1">
                      <label className={`flex items-center w-full p-3 rounded-lg border ${!selectedOrderShippingCoupon ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}>
                        <input
                          type="radio"
                          name="order-shipping-coupon"
                          checked={!selectedOrderShippingCoupon}
                          onChange={() => setSelectedOrderShippingCoupon(null)}
                          className="mr-2 accent-[#cc3333]"
                        />
                        <span className="font-medium text-gray-700">Không dùng voucher vận chuyển</span>
                      </label>
                      {orderShippingCoupons.length === 0 && <div className="text-gray-500 text-center">Không có voucher vận chuyển</div>}
                      {orderShippingCoupons.map((v: any) => (
                        <label
                          key={v.id}
                          className={`flex flex-col items-start w-full p-3 rounded-lg border ${selectedOrderShippingCoupon?.id === v.id ? 'border-[#cc3333] bg-[#fff]' : 'border-gray-200 bg-[#faeaea]'} hover:border-[#cc3333]`}
                        >
                          <input
                            type="radio"
                            name="order-shipping-coupon"
                            checked={selectedOrderShippingCoupon?.id === v.id}
                            onChange={() => setSelectedOrderShippingCoupon(v)}
                            className="mr-2"
                          />
                          <div className="flex items-center gap-2 mb-1">
                            <span className="font-bold text-[#cc3333]">{v.code}</span>
                            <span className="text-xs px-2 py-1 rounded bg-gray-200 text-gray-700 font-semibold">Vận chuyển</span>
                          </div>
                          <div className="text-sm text-gray-700">
                            {`Giảm tối đa ${v.discount.toLocaleString()}₫ phí vận chuyển`}
                          </div>
                          <div className="text-xs text-gray-500 mt-1">HSD: {v.endDate}</div>
                        </label>
                      ))}
                    </div>
                  </div>
                </div>
                <div className="flex justify-end mt-4 gap-2">
                  <button className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold" onClick={() => setShowOrderCouponModal(false)}>Đóng</button>
                  <button className="px-4 py-2 rounded bg-[#cc3333] text-white font-bold" onClick={() => setShowOrderCouponModal(false)}>Xác nhận</button>
                </div>
              </div>
            </div>
          )}
          <div className="bg-white border border-[#f5d5d5] rounded-xl p-4 flex flex-col gap-2 shadow">
            <div className="flex justify-between text-base font-semibold text-gray-700"><span>Tổng tiền hàng:</span><span>{totalProduct.toLocaleString()}₫</span></div>
            <div className="flex justify-between text-base font-semibold text-gray-700"><span>Tổng phí ship:</span><span>{totalShipping.toLocaleString()}₫</span></div>
            <div className="flex justify-between text-base font-semibold text-gray-700"><span>Tổng giảm giá:</span><span>-{totalDiscount.toLocaleString()}₫</span></div>
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