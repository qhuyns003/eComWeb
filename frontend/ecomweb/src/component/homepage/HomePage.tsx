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
  const [animateSection, setAnimateSection] = useState('');

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

  // Scroll với animation
  const scrollToSection = (sectionId: string) => {
    setAnimateSection(''); // Reset animation
    document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth' });
    
    // Trigger animation sau khi scroll
    setTimeout(() => {
      setAnimateSection(sectionId);
    }, 500);
  };

  // Expose scrollToSection globally cho Header
  useEffect(() => {
    (window as any).scrollToSection = scrollToSection;
    return () => {
      delete (window as any).scrollToSection;
    };
  }, []);

  return (
    <>
      <Header />
     
      {/* Layout với sidebar danh mục bên trái */}
      <div className="flex gap-8 px-4 md:px-8 lg:px-16 xl:px-24 py-6">
        {/* Sidebar danh mục bên trái */}
        <aside className="w-72 flex-shrink-0">
          <div className="bg-white rounded-xl shadow-lg p-4 sticky top-[90px]">
            <h3 className="text-lg font-bold text-gray-800 mb-4 pb-2 border-b">Danh mục</h3>
            <div className="space-y-2">
              {categories.map((cat, index) => {
                const icons = ['📱', '👕', '🎮', '📚', '🏠', '⚽', '💄', '🚗', '🍔', '🎵', '💊', '🎁'];
                return (
                  <button
                    key={cat.id}
                    className="w-full flex items-center gap-3 px-3 py-2 text-left rounded-lg hover:bg-[#f5d5d5] transition-colors font-medium text-gray-700 hover:text-[#cc3333]"
                  >
                    <span className="text-lg">{icons[index % icons.length]}</span>
                    <span>{cat.name}</span>
                  </button>
                );
              })}
            </div>
          </div>
        </aside>
        {/* Nội dung chính bên phải */}
        <main className="flex-1">
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
          <section id="newest-section" className="max-w-6xl mx-auto py-8 px-4">
            <h2 className="text-2xl font-bold mb-6 text-gray-800">Hàng mới</h2>
            <div className={`grid grid-cols-2 md:grid-cols-4 gap-6 ${
              animateSection === 'newest-section' ? 'animate-fade-in' : ''
            }`}>
              {newestProducts.map((product, index) => (
                <div
                  key={product.id}
                  className={`transform transition-all duration-500 ${
                    animateSection === 'newest-section' 
                      ? 'translate-y-0 opacity-100' 
                      : 'translate-y-4 opacity-90'
                  }`}
                  style={{ 
                    animationDelay: animateSection === 'newest-section' ? `${index * 0.1}s` : '0s',
                    transition: `all 0.6s ease-out ${index * 0.1}s`
                  }}
                >
                  <ProductCard
                    id={product.id}
                    name={product.name}
                    price={Number(product.price)}
                    images={product.images}
                    rating={Number(product.rating)}
                    numberOfOrder={Number(product.numberOfOrder)}
                  />
                </div>
              ))}
            </div>
          </section>
          {/* Section: Top bán chạy */}
          <section id="banchay-section" className="max-w-6xl mx-auto py-8 px-4">
            <h2 className="text-2xl font-bold mb-6 text-gray-800">Top bán chạy</h2>
            <div className={`grid grid-cols-2 md:grid-cols-4 gap-6 ${
              animateSection === 'banchay-section' ? 'animate-fade-in' : ''
            }`}>
              {topSellingProducts.map((product, index) => (
                <div
                  key={product.id}
                  className={`transform transition-all duration-500 ${
                    animateSection === 'banchay-section' 
                      ? 'translate-y-0 opacity-100' 
                      : 'translate-y-4 opacity-90'
                  }`}
                  style={{ 
                    animationDelay: animateSection === 'banchay-section' ? `${index * 0.1}s` : '0s',
                    transition: `all 0.6s ease-out ${index * 0.1}s`
                  }}
                >
                  <ProductCard
                    id={product.id}
                    name={product.name}
                    price={Number(product.price)}
                    images={product.images}
                    rating={Number(product.rating)}
                    numberOfOrder={Number(product.numberOfOrder)}
                  />
                </div>
              ))}
            </div>
          </section>
          <CompanyMarquee />
        </main>
      </div>
      <Footer />
    </>
  );
} 