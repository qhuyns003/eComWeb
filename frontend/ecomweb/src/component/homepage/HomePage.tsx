import React, { useState, useEffect } from "react";
import ProductCard from "./ProductCard";
import { useNavigate } from "react-router-dom";

const services = [
  { icon: "💸", label: "Ưu đãi mỗi ngày" },
  { icon: "🚚", label: "Giao hàng nhanh" },
  { icon: "🔒", label: "Thanh toán an toàn" },
  { icon: "🎁", label: "Quà tặng hấp dẫn" },
];

const categories = [
  { name: "Thời Trang", icon: "👗" },
  { name: "Điện Thoại", icon: "📱" },
  { name: "Laptop", icon: "💻" },
  { name: "Đồng Hồ", icon: "⌚" },
  { name: "Giày Dép", icon: "👟" },
  { name: "Gia Dụng", icon: "🍳" },
  { name: "Thể Thao", icon: "🏀" },
  { name: "Xe Máy", icon: "🛵" },
];

const banners = [
  {
    img: "https://images.unsplash.com/photo-1515168833906-d2a3b82b3029?auto=format&fit=crop&w=1200&q=80",
    alt: "Banner 1"
  },
  {
    img: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80",
    alt: "Banner 2"
  },
  {
    img: "https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=1200&q=80",
    alt: "Banner 3"
  },
];

export default function HomePage() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [bannerIdx, setBannerIdx] = useState(0);
  const navigate = useNavigate();

  // Auto slide
  useEffect(() => {
    const timer = setTimeout(() => {
      setBannerIdx((prev) => (prev + 1) % banners.length);
    }, 4000);
    return () => clearTimeout(timer);
  }, [bannerIdx]);

  return (
    <>
      <nav className="h-[70px] relative w-full px-6 md:px-16 lg:px-24 xl:px-32 flex items-center justify-between z-30 bg-gradient-to-r from-[#cc3333] to-pink-500 transition-all">
        <a href="#">
          <img className="h-9" src="https://raw.githubusercontent.com/prebuiltui/prebuiltui/main/assets/dummyLogo/dummyLogoWhite.svg" alt="dummyLogoWhite" />
        </a>

        <form className="flex items-center border pl-4 gap-2 bg-white border-gray-500/30 h-[46px] rounded-full overflow-hidden max-w-md w-full">
          <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 30 30" fill="#6B7280">
            <path d="M13 3C7.489 3 3 7.489 3 13s4.489 10 10 10a9.95 9.95 0 0 0 6.322-2.264l5.971 5.971a1 1 0 1 0 1.414-1.414l-5.97-5.97A9.95 9.95 0 0 0 23 13c0-5.511-4.489-10-10-10m0 2c4.43 0 8 3.57 8 8s-3.57 8-8 8-8-3.57-8-8 3.57-8 8-8"/>
          </svg>
          <input type="text" className="w-full h-full outline-none text-sm text-gray-500" placeholder="Tìm kiếm sản phẩm..." />
          <button type="submit" className="bg-[#cc3333] w-32 h-9 rounded-full text-sm text-white mr-[5px] hover:bg-[#b82d2d] transition">Search</button>
        </form>

        <div className="flex gap-3">
          <button className="bg-[#cc3333] w-32 h-9 rounded-full text-sm text-white hover:bg-[#b82d2d] transition shadow">Đăng ký</button>
          <button className="bg-[#cc3333] w-32 h-9 rounded-full text-sm text-white hover:bg-[#b82d2d] transition shadow" onClick={() => navigate("/login")}>Đăng nhập</button>
        </div>

        <button
          aria-label="menu-btn"
          type="button"
          className="menu-btn inline-block md:hidden active:scale-90 transition bg-[#cc3333] p-2 rounded"
          onClick={() => setMobileOpen((v) => !v)}
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 30 30" fill="#fff">
            <path d="M3 7a1 1 0 1 0 0 2h24a1 1 0 1 0 0-2zm0 7a1 1 0 1 0 0 2h24a1 1 0 1 0 0-2zm0 7a1 1 0 1 0 0 2h24a1 1 0 1 0 0-2z" />
          </svg>
        </button>

        {mobileOpen && (
          <div className="mobile-menu absolute top-[70px] left-0 w-full bg-gradient-to-r from-[#cc3333] to-pink-500 p-6 md:hidden animate-fade-in z-40">
            <ul className="flex flex-col space-y-4 text-white text-lg">
              <li><a href="#" className="text-sm">Home</a></li>
              <li><a href="#" className="text-sm">Services</a></li>
              <li><a href="#" className="text-sm">Portfolio</a></li>
              <li><a href="#" className="text-sm">Pricing</a></li>
            </ul>
            <button type="button" className="bg-[#cc3333] text-white mt-6 inline md:hidden text-sm hover:bg-[#b82d2d] active:scale-95 transition-all w-40 h-11 rounded-full shadow">
              Get started
            </button>
          </div>
        )}
      </nav>
      {/* Category bar dưới header */}
      <div className="w-full bg-white shadow-sm px-4 md:px-16 lg:px-24 xl:px-32 py-2 border-b border-gray-100">
        <div className="flex gap-3 overflow-x-auto scrollbar-thin scrollbar-thumb-gray-200 pb-1">
          {categories.map((cat) => (
            <button
              key={cat.name}
              className="flex items-center gap-2 px-4 py-2 rounded-full bg-gray-50 border border-gray-200 text-gray-700 hover:bg-[#cc3333] hover:text-white transition whitespace-nowrap shadow-sm min-w-[110px] justify-center"
            >
              <span className="text-lg">{cat.icon}</span>
              <span className="text-sm font-medium">{cat.name}</span>
            </button>
          ))}
        </div>
      </div>
      {/* Banner carousel */}
      <div className="w-full flex justify-center bg-white py-6">
        <div className="relative w-full max-w-4xl rounded-2xl overflow-hidden shadow-xl">
          <img
            src={banners[bannerIdx].img}
            alt={banners[bannerIdx].alt}
            className="w-full h-56 md:h-80 object-cover transition-all duration-500"
          />
          {/* Nút chuyển trái */}
          <button
            className="absolute top-1/2 left-3 -translate-y-1/2 bg-white/80 hover:bg-[#cc3333] hover:text-white text-gray-700 rounded-full p-2 shadow transition"
            onClick={() => setBannerIdx((prev) => (prev - 1 + banners.length) % banners.length)}
            aria-label="prev"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" className="w-6 h-6">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          {/* Nút chuyển phải */}
          <button
            className="absolute top-1/2 right-3 -translate-y-1/2 bg-white/80 hover:bg-[#cc3333] hover:text-white text-gray-700 rounded-full p-2 shadow transition"
            onClick={() => setBannerIdx((prev) => (prev + 1) % banners.length)}
            aria-label="next"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" className="w-6 h-6">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
          {/* Dots */}
          <div className="absolute bottom-3 left-1/2 -translate-x-1/2 flex gap-2">
            {banners.map((_, idx) => (
              <span
                key={idx}
                className={`w-3 h-3 rounded-full ${idx === bannerIdx ? 'bg-[#cc3333]' : 'bg-gray-300'} block transition`}
              />
            ))}
          </div>
        </div>
      </div>
      {/* Section: Hàng mới */}
      <section className="max-w-6xl mx-auto py-8 px-4">
        <h2 className="text-2xl font-bold mb-6 text-gray-800">Hàng mới</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, idx) => (
            <ProductCard
              key={idx}
              image={`https://picsum.photos/seed/new${idx}/220/220`}
              alt={`Hàng mới ${idx + 1}`}
              name={`Sản phẩm mới ${idx + 1}`}
              price={`${(199000 + idx * 10000).toLocaleString()}₫`}
            />
          ))}
        </div>
      </section>
      {/* Section: Top bán chạy */}
      <section className="max-w-6xl mx-auto py-8 px-4">
        <h2 className="text-2xl font-bold mb-6 text-gray-800">Top bán chạy</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, idx) => (
            <ProductCard
              key={idx}
              image={`https://picsum.photos/seed/top${idx}/220/220`}
              alt={`Top bán chạy ${idx + 1}`}
              name={`Sản phẩm hot ${idx + 1}`}
              price={`${(299000 + idx * 15000).toLocaleString()}₫`}
            />
          ))}
        </div>
      </section>
      {/* Footer */}
      <footer className="px-6 md:px-16 lg:px-24 xl:px-32 pt-8 w-full text-gray-100 bg-[#cc3333]">
        <div className="flex flex-col md:flex-row justify-between w-full gap-10 border-b border-[#fff]/30 pb-6">
          <div className="md:max-w-96">
            <img className="h-9" src="https://raw.githubusercontent.com/prebuiltui/prebuiltui/main/assets/dummyLogo/dummyLogoWhite.svg" alt="dummyLogoDark" />
            <p className="mt-6 text-sm text-gray-100/90">
              Lorem Ipsum is simply dummy text of the printing and typesetting industry.
              Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
              when an unknown printer took a galley of type and scrambled it to make a type specimen book.
            </p>
          </div>
          <div className="flex-1 flex items-start md:justify-end gap-20">
            <div>
              <h2 className="font-semibold mb-5 text-white">Company</h2>
              <ul className="text-sm space-y-2">
                <li><a href="#" className="hover:text-pink-200 transition">Home</a></li>
                <li><a href="#" className="hover:text-pink-200 transition">About us</a></li>
                <li><a href="#" className="hover:text-pink-200 transition">Contact us</a></li>
                <li><a href="#" className="hover:text-pink-200 transition">Privacy policy</a></li>
              </ul>
            </div>
            <div>
              <h2 className="font-semibold mb-5 text-white">Get in touch</h2>
              <div className="text-sm space-y-2">
                <p>+1-212-456-7890</p>
                <p>contact@example.com</p>
              </div>
            </div>
          </div>
        </div>
        <p className="pt-4 text-center text-xs md:text-sm pb-5 text-gray-100/80">
          Copyright 2024 © Company name. All Right Reserved.
        </p>
      </footer>
    </>
  );
} 