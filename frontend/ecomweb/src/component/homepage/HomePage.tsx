import React, { useState, useEffect } from "react";
import ProductCard from "./ProductCard";
import UserMenu from "./UserMenu";
import { useNavigate } from "react-router-dom";
import { useAppSelector } from "../../store/hooks";
import { selectUser } from "../../store/features/userSlice";
import { getCategories, getTopSellingProducts, getNewestProducts } from "../../api/api";
import Header from "../layout/Header";
import Footer from "../layout/Footer";
import CompanyMarquee from "./CompanyMarquee";

const services = [
  { icon: "💸", label: "Ưu đãi mỗi ngày" },
  { icon: "🚚", label: "Giao hàng nhanh" },
  { icon: "🔒", label: "Thanh toán an toàn" },
  { icon: "🎁", label: "Quà tặng hấp dẫn" },
];

type Category = {
  id: number;
  name: string;
};

type ProductOverviewResponse = {
  id: string;
  name: string;
  price: number;
  images: Array<{
    id: string;
    url: string;
    isMain: boolean;
  }>;
  rating: number;
  numberOfOrder: string;
};

const MAX_VISIBLE = 8;

const banners = [
  {
    img: "/360_F_465465254_1pN9MGrA831idD6zIBL7q8rnZZpUCQTy.jpg",
    alt: "Banner 1"
  },
  {
    img: "/867pIkbEsTAIq.png!w700wp",
    alt: "Banner 2"
  },
  {
    img: "/grocery-sale-retail-or-e-commerce-banner-ad-design-template-67720435bb809be27f46dfb1dd44c6fa_screen.jpg",
    alt: "Banner 3"
  },
];

export default function HomePage() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [bannerIdx, setBannerIdx] = useState(0);
  const navigate = useNavigate();
  const user = useAppSelector(selectUser);
  const [categories, setCategories] = useState<Category[]>([]);
  const [showAllCategories, setShowAllCategories] = useState(false);
  const [topSellingProducts, setTopSellingProducts] = useState<ProductOverviewResponse[]>([]);
  const [newestProducts, setNewestProducts] = useState<ProductOverviewResponse[]>([]);

  // Auto slide
  useEffect(() => {
    const timer = setTimeout(() => {
      setBannerIdx((prev) => (prev + 1) % banners.length);
    }, 4000);
    return () => clearTimeout(timer);
  }, [bannerIdx]);

  useEffect(() => {
    getCategories()
      .then((res) => setCategories(res.data.result))
      .catch((err) => {
        console.error(err);
      });
  }, []);

  useEffect(() => {
    getTopSellingProducts(8)
      .then((res) => setTopSellingProducts(res.data.result))
      .catch((err) => {
        console.error(err);
      });
  }, []);

  useEffect(() => {
    getNewestProducts(8)
      .then(res => setNewestProducts(res.data.result))
      .catch(err => console.error(err));
  }, []);

  return (
    <>
      <Header />
      {/* Category bar dưới header */}
      <div className="w-full bg-white shadow-sm px-4 md:px-16 lg:px-24 xl:px-32 py-2 border-b border-gray-100 relative">
        <div className="flex gap-3 overflow-x-auto scrollbar-thin scrollbar-thumb-gray-200 pb-1">
          {(showAllCategories ? categories : categories.slice(0, MAX_VISIBLE)).map((cat) => (
            <button
              key={cat.id}
              className="flex items-center gap-2 px-4 py-2 rounded-full bg-gray-50 border border-gray-200 text-gray-700 hover:bg-[#cc3333] hover:text-white transition whitespace-nowrap shadow-sm min-w-[110px] justify-center"
            >
            
              <span className="text-sm font-medium">{cat.name}</span>
            </button>
          ))}
          {categories.length > MAX_VISIBLE && !showAllCategories && (
            <button
              className="flex items-center gap-2 px-4 py-2 rounded-full bg-[#cc3333] text-white font-semibold border-2 border-[#cc3333] shadow-lg min-w-[110px] justify-center hover:bg-[#b82d2d] transition"
              onClick={() => setShowAllCategories(true)}
            >
              <span className="text-sm font-medium">Xem thêm</span>
              <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
          )}
        </div>
        {/* Dropdown toàn bộ category */}
        {showAllCategories && (
          <div className="absolute left-0 w-full bg-white shadow-lg rounded p-4 z-50 top-full mt-2 flex flex-wrap gap-2">
            {/* Nút Đóng nổi bật */}
            <button
              className="absolute top-2 right-4 bg-[#cc3333] text-white rounded-full p-2 shadow-lg z-50 hover:bg-[#b82d2d] transition"
              onClick={() => setShowAllCategories(false)}
              aria-label="Đóng"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
            {categories.map((cat) => (
              <button
                key={cat.id}
                className="flex items-center gap-2 px-4 py-2 rounded-full bg-gray-50 border border-gray-200 text-gray-700 hover:bg-[#cc3333] hover:text-white transition whitespace-nowrap shadow-sm min-w-[110px] justify-center"
              >
               
                <span className="text-sm font-medium">{cat.name}</span>
              </button>
            ))}
          </div>
        )}
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
          {newestProducts.map(product => (
            <ProductCard
              key={product.id}
              id={product.id}
              name={product.name}
              price={Number(product.price)}
              images={product.images}
              rating={Number(product.rating)}
              numberOfOrder={Number(product.numberOfOrder)}
            />
          ))}
        </div>
      </section>
      {/* Section: Top bán chạy */}
      <section className="max-w-6xl mx-auto py-8 px-4">
        <h2 className="text-2xl font-bold mb-6 text-gray-800">Top bán chạy</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {topSellingProducts.map((product) => (
            <ProductCard
              key={product.id}
              id={product.id}
              name={product.name}
              price={Number(product.price)}
              images={product.images}
              rating={Number(product.rating)}
              numberOfOrder={Number(product.numberOfOrder)}
            />
          ))}
        </div>
      </section>
      <CompanyMarquee />
      <Footer />
    </>
  );
} 