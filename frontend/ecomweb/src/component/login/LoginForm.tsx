import React from "react";
import { useAuth } from '../../contexts/AuthContext';

interface LoginFormProps {
  onSubmit?: (username: string, password: string) => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onSubmit }) => {
  const { login, isLoading } = useAuth();

  const handleKeycloakLogin = () => {
    login(); // Redirect đến Keycloak login
  };

  const handleTraditionalSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Có thể giữ lại form truyền thống như fallback
    // hoặc chỉ dùng Keycloak
    alert('Vui lòng sử dụng đăng nhập SSO');
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center relative bg-cover bg-center" style={{ backgroundImage: 'url(/realistic-polygonal-background_23-2148921891.avif)' }}>
      <div className="absolute inset-0 bg-black/30 z-0" />
      <div className="relative z-10 w-full flex items-center justify-center">
        <div className="bg-white max-w-96 mx-auto md:p-8 p-5 rounded-2xl shadow-lg border border-[#cc3333]/10 mt-12 animate-fade-in backdrop-blur-sm bg-opacity-90">
          <h2 className="text-3xl font-bold mb-2 text-center text-[#cc3333] tracking-tight">Chào mừng trở lại!</h2>
          <p className="text-center text-gray-500 mb-7">Đăng nhập để tiếp tục mua sắm cùng chúng tôi</p>
          
          {/* Nút đăng nhập SSO chính */}
          <div className="space-y-4 mb-6">
            <button 
              type="button" 
              onClick={handleKeycloakLogin}
              className="w-full text-white bg-[#cc3333] hover:bg-[#b82d2d] focus:ring-4 focus:outline-none focus:ring-[#f05252] font-medium rounded-lg text-sm px-5 py-3 text-center disabled:opacity-60"
              disabled={isLoading}
            >
              {isLoading ? 'Đang khởi tạo...' : 'Đăng nhập với SSO'}
            </button>
          </div>

          <div className="flex items-center my-5">
            <div className="flex-1 h-px bg-gray-200" />
            <span className="mx-3 text-gray-400 text-xs">Hoặc đăng nhập truyền thống</span>
            <div className="flex-1 h-px bg-gray-200" />
          </div>

          {/* Form truyền thống (có thể ẩn hoặc giữ lại) */}
          <form onSubmit={handleTraditionalSubmit} className="space-y-4">
            <input
              id="username"
              className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
              type="text"
              placeholder="Tên đăng nhập"
              required
              disabled={true}
            />
            <input
              id="password"
              className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
              type="password"
              placeholder="Mật khẩu"
              required
              disabled={true}
            />
            <div className="text-right pt-1">
              <a className="text-[#cc3333] hover:underline text-sm font-medium transition opacity-50" href="#">Quên mật khẩu?</a>
            </div>
            <button 
              type="submit" 
              className="w-full text-white bg-gray-400 font-medium rounded-lg text-sm px-5 py-2.5 text-center cursor-not-allowed" 
              disabled={true}
            >
              Đăng nhập truyền thống (đã tắt)
            </button>
          </form>

          <div className="flex items-center my-5">
            <div className="flex-1 h-px bg-gray-200" />
            <span className="mx-3 text-gray-400 text-xs">Hoặc đăng nhập với</span>
            <div className="flex-1 h-px bg-gray-200" />
          </div>
          <div className="flex flex-col gap-3">
            <button type="button" className="w-full flex items-center gap-3 justify-center bg-black py-2.5 rounded-full text-white font-medium hover:opacity-90 transition">
              <img className="h-5 w-5" src="https://raw.githubusercontent.com/prebuiltui/prebuiltui/main/assets/login/appleLogo.png" alt="appleLogo" />
              Đăng nhập với Apple
            </button>
            <button type="button" className="w-full flex items-center gap-3 justify-center bg-white border border-[#cc3333]/30 py-2.5 rounded-full text-[#cc3333] font-medium hover:bg-[#cc3333]/10 transition">
              <img className="h-5 w-5" src="https://raw.githubusercontent.com/prebuiltui/prebuiltui/main/assets/login/googleFavicon.png" alt="googleFavicon" />
              Đăng nhập với Google
            </button>
          </div>
          <p className="text-sm font-light text-gray-500 dark:text-gray-400">
            Chưa có tài khoản? <a href="/register" className="font-medium text-[#cc3333] hover:underline">Đăng ký ngay</a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginForm; 