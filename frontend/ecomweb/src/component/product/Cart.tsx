import React from "react";
import Header from "../layout/Header";
import Footer from "../layout/Footer";

const cartItems = [
  {
    id: 1,
    name: "Sản phẩm A",
    price: 100000,
    quantity: 2,
    image: "/vite.svg",
  },
  {
    id: 2,
    name: "Sản phẩm B",
    price: 200000,
    quantity: 1,
    image: "/360_F_465465254_1pN9MGrA831idD6zIBL7q8rnZZpUCQTy.jpg",
  },
];

const Cart: React.FC = () => {
  const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <>
      <Header />
      <main className="min-h-[60vh] bg-gray-50 py-8 px-2 md:px-0 flex justify-center">
        <div className="w-full max-w-4xl">
          <h2 className="text-2xl font-bold mb-6 text-[#cc3333] text-center">🛒 Giỏ hàng của bạn</h2>
          {cartItems.length === 0 ? (
            <div className="bg-white rounded-xl shadow p-8 text-center text-gray-500">
              Giỏ hàng trống.
            </div>
          ) : (
            <div className="bg-white rounded-xl shadow-lg p-4 md:p-8">
              <div className="hidden md:grid grid-cols-12 gap-4 pb-3 border-b font-semibold text-gray-700">
                <div className="col-span-5">Sản phẩm</div>
                <div className="col-span-2 text-center">Số lượng</div>
                <div className="col-span-2 text-right">Đơn giá</div>
                <div className="col-span-2 text-right">Thành tiền</div>
                <div className="col-span-1"></div>
              </div>
              <div className="divide-y">
                {cartItems.map(item => (
                  <div key={item.id} className="grid grid-cols-12 gap-4 py-4 items-center hover:bg-gray-50 transition">
                    {/* Ảnh + tên sản phẩm */}
                    <div className="col-span-5 flex items-center gap-4">
                      <img src={item.image} alt={item.name} className="w-16 h-16 object-cover rounded-lg border" />
                      <div>
                        <a href="#" className="font-semibold text-gray-900 hover:text-[#cc3333] transition">{item.name}</a>
                        <div className="text-xs text-gray-400">Mã: {item.id}</div>
                      </div>
                    </div>
                    {/* Số lượng */}
                    <div className="col-span-2 flex items-center justify-center gap-2">
                      <button className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center text-lg font-bold">-</button>
                      <span className="w-8 text-center">{item.quantity}</span>
                      <button className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center text-lg font-bold">+</button>
                    </div>
                    {/* Đơn giá */}
                    <div className="col-span-2 text-right text-gray-700 font-medium">{item.price.toLocaleString()}₫</div>
                    {/* Thành tiền */}
                    <div className="col-span-2 text-right text-[#cc3333] font-bold">{(item.price * item.quantity).toLocaleString()}₫</div>
                    {/* Xóa */}
                    <div className="col-span-1 flex justify-end">
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
                <button className="bg-gradient-to-r from-[#cc3333] to-pink-500 text-white font-semibold px-10 py-3 rounded-full shadow-lg hover:from-[#b82d2d] hover:to-pink-400 transition text-lg disabled:opacity-60" disabled={cartItems.length === 0}>
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