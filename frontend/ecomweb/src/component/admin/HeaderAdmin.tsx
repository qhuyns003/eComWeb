import React from "react";
import UserMenu from "../homepage/UserMenu";
import { useNavigate } from "react-router-dom";
import { useAppSelector } from "../../store/hooks";
import { selectUser } from "../../store/features/userSlice";

const HeaderAdmin: React.FC = () => {
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const navigate = useNavigate();
  const user = useAppSelector(selectUser);

  return (
    <nav className="h-[70px] relative w-full px-6 md:px-16 lg:px-24 xl:px-32 flex items-center justify-between z-30 bg-gradient-to-r from-[#cc3333] to-pink-500 transition-all">
      <a href="#">
        <img className="h-9" src="https://raw.githubusercontent.com/prebuiltui/prebuiltui/main/assets/dummyLogo/dummyLogoWhite.svg" alt="dummyLogoWhite" />
      </a>

      {/* Chữ Shop Admin nổi bật, hiện đại */}
      <div className="flex-1 flex items-center justify-center">
        <h1 className="text-white text-4xl font-extrabold tracking-wide drop-shadow-lg select-none" style={{
          letterSpacing: '0.05em',
          textShadow: '0 2px 16px rgba(204,51,51,0.25), 0 1px 0 #fff'
        }}>
          Shop Admin
        </h1>
      </div>

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

export default HeaderAdmin; 