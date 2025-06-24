import React, { useState } from "react";
import { login, getMyInfo } from '../../api/api';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch } from '../../store/hooks';
import { setUser } from '../../store/features/userSlice';

interface LoginFormProps {
  onSubmit?: (username: string, password: string) => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onSubmit }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (onSubmit) onSubmit(username, password);
    setLoading(true); // việc re-render không làm ngắt hàm đang chạy, nên có thể re-render trong lúc chờ api
    try {
      const res = await login(username, password); // giao dien có thể rerender trong lúc chờ api
      const token = res.data.result.token; 
      localStorage.setItem('token', token);
    
      // Lấy thông tin user và cập nhật Redux store
      const userRes = await getMyInfo(token);
  
      dispatch(setUser(userRes.data.result));
      
      
      navigate('/');
    } catch (err: any) {
      alert(err?.response?.data?.message || 'Đăng nhập thất bại!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center relative bg-cover bg-center" style={{ backgroundImage: 'url(/realistic-polygonal-background_23-2148921891.avif)' }}>
      <div className="absolute inset-0 bg-black/30 z-0" />
      <div className="relative z-10 w-full flex items-center justify-center">
        <div className="bg-white max-w-96 mx-auto md:p-8 p-5 rounded-2xl shadow-lg border border-[#cc3333]/10 mt-12 animate-fade-in backdrop-blur-sm bg-opacity-90">
          <h2 className="text-3xl font-bold mb-2 text-center text-[#cc3333] tracking-tight">Chào mừng trở lại!</h2>
          <p className="text-center text-gray-500 mb-7">Đăng nhập để tiếp tục mua sắm cùng chúng tôi</p>
          <form onSubmit={handleSubmit} className="space-y-4">
            <input
              id="username"
              className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
              type="text"
              placeholder="Tên đăng nhập"
              required
              value={username}
              onChange={e => setUsername(e.target.value)}
              disabled={loading}
            />
            <input
              id="password"
              className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
              type="password"
              placeholder="Mật khẩu"
              required
              value={password}
              onChange={e => setPassword(e.target.value)}
              disabled={loading}
            />
            <div className="text-right pt-1">
              <a className="text-[#cc3333] hover:underline text-sm font-medium transition" href="#">Quên mật khẩu?</a>
            </div>
            <button type="submit" className="w-full text-white bg-[#cc3333] hover:bg-[#b82d2d] focus:ring-4 focus:outline-none focus:ring-[#f05252] font-medium rounded-lg text-sm px-5 py-2.5 text-center disabled:opacity-60" disabled={loading}>
              {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
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