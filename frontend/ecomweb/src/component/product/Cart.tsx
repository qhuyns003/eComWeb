import React, { useEffect, useState } from "react";
import Header from "../layout/Header";
import Footer from "../layout/Footer";
import { getCartItems } from '../../api/api';
import { useAppDispatch } from '../../store/hooks';
import { setOrderShopGroups } from '../../store/features/orderSlice';
import { useNavigate } from 'react-router-dom';
import type { OrderShopGroup } from '../../store/features/orderSlice';

// Định nghĩa kiểu dữ liệu cho item trong giỏ hàng
interface CartItem {
  id: string;
  quantity: number;
  shop: {
    id: string;
    name: string;
    description?: string | null;
    address?: string | null;
    status?: string | null;
    createdAt?: string | null;
  };
  variantId: string;
  productName: string;
  variantPrice: number;
  imageUrl: string;
  weight: number;
  length: number;
  width: number;
  height: number;
  detailAttributes: string[];
}

const Cart: React.FC = () => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedIds, setSelectedIds] = useState<string[]>([]);

  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const handleSelect = (id: string) => {
    setSelectedIds(prev =>
      prev.includes(id) ? prev.filter(_id => _id !== id) : [...prev, id]
    );
  };

  const handleSelectAll = () => {
    if (selectedIds.length === cartItems.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(cartItems.map(item => item.id));
    }
  };

  useEffect(() => {
    setLoading(true);
    getCartItems()
      .then(res => {
        const data = res.data;
        if (data.code === 1000 && Array.isArray(data.result)) {
          setCartItems(data.result);
        } else {
          setError("Không lấy được dữ liệu giỏ hàng.");
        }
        setLoading(false);
      })
      .catch(() => {
        setError("Lỗi kết nối đến server.");
        setLoading(false);
      });
  }, []);

  // Tính tổng tiền
  const total = cartItems.reduce((sum, item) => sum + item.variantPrice * item.quantity, 0);

  return (
    <>
      <Header />
      <main className="min-h-[60vh] bg-gray-50 py-8 px-2 md:px-0 flex justify-center">
        <div className="w-full max-w-5xl">
          <h2 className="text-2xl font-bold mb-6 text-[#cc3333] text-center">🛒 Giỏ hàng của bạn</h2>
          {loading ? (
            <div className="bg-white rounded-xl shadow p-8 text-center text-gray-500">Đang tải dữ liệu...</div>
          ) : error ? (
            <div className="bg-white rounded-xl shadow p-8 text-center text-red-500">{error}</div>
          ) : cartItems.length === 0 ? (
            <div className="bg-white rounded-xl shadow p-8 text-center text-gray-500">Giỏ hàng trống.</div>
          ) : (
            <div className="bg-white rounded-xl shadow-lg p-4 md:p-8">
              <div className="hidden md:grid grid-cols-12 gap-6 pb-3 border-b font-semibold text-gray-700 items-center">
                <div className="col-span-1 flex justify-center px-2">
                  <input
                    type="checkbox"
                    checked={selectedIds.length === cartItems.length && cartItems.length > 0}
                    onChange={handleSelectAll}
                    className="accent-[#cc3333] w-5 h-5"
                  />
                </div>
                <div className="col-span-5 px-2">Sản phẩm</div>
                <div className="col-span-2 px-2">Thuộc tính</div>
                <div className="col-span-1 text-center px-2">Số lượng</div>
                <div className="col-span-1 text-right px-2">Đơn giá</div>
                <div className="col-span-1 text-right px-2">Thành tiền</div>
                <div className="col-span-1 px-2"></div>
              </div>
              <div className="divide-y">
                {cartItems.map(item => (
                  <div key={item.id} className="grid grid-cols-12 gap-6 py-4 items-center hover:bg-gray-50 transition">
                    {/* Checkbox */}
                    <div className="col-span-1 flex justify-center px-2">
                      <input
                        type="checkbox"
                        checked={selectedIds.includes(item.id)}
                        onChange={() => handleSelect(item.id)}
                        className="accent-[#cc3333] w-5 h-5"
                      />
                    </div>
                    {/* Ảnh + tên sản phẩm + shop */}
                    <div className="col-span-5 flex items-center gap-4 px-2 min-w-0">
                      <img
                        src={
                          item.imageUrl.startsWith('http')
                            ? item.imageUrl
                            : `http://localhost:8080/uploads/${item.imageUrl}`
                        }
                        alt={item.productName}
                        className="w-16 h-16 object-cover rounded-lg border"
                      />
                      <div className="min-w-0">
                        <div className="font-semibold text-gray-900 hover:text-[#cc3333] transition truncate max-w-[220px]">{item.productName}</div>
                        <div className="text-xs text-gray-400 truncate">{item.shop.name}</div>
                      </div>
                    </div>
                    {/* Thuộc tính */}
                    <div className="col-span-2 text-sm text-gray-700 px-2 truncate">{item.detailAttributes?.join(', ') || '-'}</div>
                    {/* Số lượng */}
                    <div className="col-span-1 flex items-center justify-center gap-2 px-2">{item.quantity}</div>
                    {/* Đơn giá */}
                    <div className="col-span-1 text-right text-gray-700 font-medium px-2">{item.variantPrice.toLocaleString()}₫</div>
                    {/* Thành tiền */}
                    <div className="col-span-1 text-right text-[#cc3333] font-bold px-2">{(item.variantPrice * item.quantity).toLocaleString()}₫</div>
                    {/* Xóa */}
                    <div className="col-span-1 flex justify-end px-2">
                      <button className="p-2 hover:bg-red-50 rounded-full" title="Xóa">
                        <svg className="w-5 h-5 text-red-400 hover:text-red-600" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
              {/* Tổng cộng + Thanh toán */}
              <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mt-8 border-t pt-6">
                <div className="text-xl font-bold text-[#cc3333]">Tổng cộng: {total.toLocaleString()}₫</div>
                <button
                  className="bg-gradient-to-r from-[#cc3333] to-pink-500 text-white font-semibold px-10 py-3 rounded-full shadow-lg hover:from-[#b82d2d] hover:to-pink-400 transition text-lg disabled:opacity-60"
                  disabled={selectedIds.length === 0}
                  onClick={() => {
                    const selectedItems = cartItems.filter(item => selectedIds.includes(item.id));
                    const shopMap: { [shopId: string]: { shop: any; products: any[] } } = {};
                    selectedItems.forEach(item => {
                      if (!shopMap[item.shop.id]) {
                        shopMap[item.shop.id] = {
                          shop: item.shop,
                          products: []
                        };
                      }
                      shopMap[item.shop.id].products.push({
                        id: item.variantId,
                        name: item.productName,
                        price: item.variantPrice,
                        quantity: item.quantity,
                        image: item.imageUrl,
                        attrs: item.detailAttributes,
                        weight: item.weight,
                        length: item.length,
                        width: item.width,
                        height: item.height
                      });
                    });
                    const orderShopGroups = Object.values(shopMap);
                    dispatch(setOrderShopGroups(orderShopGroups as OrderShopGroup[]));
                    navigate('/checkout');
                  }}
                >
                  Thanh toán ngay
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </>
  );
};

export default Cart;