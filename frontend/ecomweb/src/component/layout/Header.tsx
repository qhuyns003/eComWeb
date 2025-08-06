import React from "react";
import UserMenu from "../homepage/UserMenu";
import { useNavigate, useLocation } from "react-router-dom";
import { useAppSelector } from "../../store/hooks";
import { selectUser } from "../../store/features/userSlice";

const Header: React.FC = () => {
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const navigate = useNavigate();
  const user = useAppSelector(selectUser);
  const location = useLocation();

  return (
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
      {location.pathname === '/' && (
        <div className="flex items-center gap-3 mx-4">
                      <button
              className="font-medium text-white bg-transparent hover:bg-white/20 hover:text-[#cc3333] text-base px-3 py-1 rounded-full transition flex items-center gap-1"
              onClick={() => (window as any).scrollToSection?.('newest-section')}
            >
              Hàng mới
            </button>
            <button
              className="font-medium text-white bg-transparent hover:bg-white/20 hover:text-[#cc3333] text-base px-3 py-1 rounded-full transition flex items-center gap-1"
              onClick={() => (window as any).scrollToSection?.('banchay-section')}
            >
              Bán chạy
            </button>
        </div>
      )}
      <div className="flex gap-3">
        {user ? (
          <UserMenu />
        ) : (
          <>
            <button
              className="bg-[#cc3333] w-32 h-9 rounded-full text-sm text-white hover:bg-[#b82d2d] transition shadow"
              onClick={() => navigate('/register')}
            >
              Đăng ký
            </button>
            <button
              className="bg-[#cc3333] w-32 h-9 rounded-full text-sm text-white hover:bg-[#b82d2d] transition shadow"
              onClick={() => navigate('/login')}
            >
              Đăng nhập
            </button>
          </>
        )}
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
  );
};

export default Header; 