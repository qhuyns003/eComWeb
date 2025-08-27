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
    img: "https://unibenfoods.com/files/pages/home/slider/web-banner-boncha-ambassador-ngang-2000x1030px.jpg",
    alt: "Banner 5"
  },
  // { 
  
  //   img: "https://scontent.fhan5-6.fna.fbcdn.net/v/t39.30808-6/500836527_689158607200777_5449153216550391359_n.jpg?_nc_cat=105&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=A7z8xfR8W9MQ7kNvwEj3jSv&_nc_oc=Adkcf5XtnwPE7AOM08h7_a9cuNQhYy7wbPqGBfs-ZbqWP1TSDwhA3VCk2EnzkI5Aa5s&_nc_zt=23&_nc_ht=scontent.fhan5-6.fna&_nc_gid=DkN4SmOravLd-WViQuJs9Q&oh=00_AfWRTjgTXVRy9tI-JDeS4-3Ba9-rWnUi5C8YvlD2EA2VAg&oe=689FD6A3",
  //   alt: "Banner 4"
  // },
  // { 
  
  //   img: "https://scontent.fhan5-8.fna.fbcdn.net/v/t39.30808-6/506351717_1314234340704107_750407303984346571_n.png?_nc_cat=106&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=F_uUy4K-ib8Q7kNvwFCLAiE&_nc_oc=AdnJIBf3O96FJJY94r14Jx6LesB6xAbKz_VRIdorHDzlVbzkkdisiRPqchkL4tjX1rI&_nc_zt=23&_nc_ht=scontent.fhan5-8.fna&_nc_gid=FfKYVFWBl4DdXjYOWXBhVA&oh=00_AfUaqCvIMUZXHgYQA3d_6-qeLLnsEV9IqAXa12hNmvHFHQ&oe=689FCD3F",
  //   alt: "Banner 4"
  // },
  {
    img: "https://scontent.fhan5-6.fna.fbcdn.net/v/t39.30808-6/508692111_1156566123176558_6880270108761767108_n.jpg?_nc_cat=107&ccb=1-7&_nc_sid=cc71e4&_nc_ohc=8ouhAuveWL4Q7kNvwEY8o5M&_nc_oc=AdmmT8LhyzcwGWKBTm5xhEZ1ce1RLFlRpj8Wh-PV6DUMHEnE6ylem_g71GO5_o86Hpw&_nc_zt=23&_nc_ht=scontent.fhan5-6.fna&_nc_gid=_C_OhD8BNq360199eoelkg&oh=00_AfX0JCGDl06G4Plq1ST7BEt0k4GpCZnCO3JTDsF1ru-O0w&oe=68A87890",
    alt: "Banner 1"
  },
  // {
  //   img: "https://scontent.fhan5-8.fna.fbcdn.net/v/t39.30808-6/481254173_1203250324804758_5343216246685135028_n.jpg?_nc_cat=106&ccb=1-7&_nc_sid=86c6b0&_nc_ohc=8rZQKdE1c88Q7kNvwGMl9tI&_nc_oc=AdnawW4sH7WFOz5p99f0k0o_hleECJqrJXhh2Zbs2H7oywvRHiW1bg3YS32khDxCazA&_nc_zt=23&_nc_ht=scontent.fhan5-8.fna&_nc_gid=2Ldxw5D-9HB1RPVp2N27-g&oh=00_AfVov-GaX2TzCjoWjfEf7VHghDZsDAxwp9sg6mKk6HKCyQ&oe=689FB6C0",
  //   alt: "Banner 1"
  // },
  {
    img: "https://marketingai.mediacdn.vn/603488451643117568/2023/10/30/oishi-pinattsu-16986519290211830151110.jpg",
    alt: "Banner 1"
  },
  {
    img: "https://iphonebencat.com/uploads/source//slider/banner-iphone-16-price-2tmobile-1024x406.webp",
    alt: "Banner 2"
  },
  {
    img: "https://media.thanhtra.com.vn/public/uploads/2024/10/31/6723331f673a5a8dfe0d7d68.jpg?w=1319",
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
    }, 2500);
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