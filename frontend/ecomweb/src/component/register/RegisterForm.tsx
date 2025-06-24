import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { register } from '../../api/api';

const RegisterForm: React.FC = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    username: '',
    dob: '',
    phone: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (formData.password !== formData.confirmPassword) {
      setError('Mật khẩu không khớp!');
      return;
    }
    setLoading(true);
    try {
      
      await register(formData);
      alert('Đăng ký thành công! Vui lòng đăng nhập.');
      navigate('/login');
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Đăng ký thất bại!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center relative bg-cover bg-center" style={{ backgroundImage: 'url(/realistic-polygonal-background_23-2148921891.avif)' }}>
      <div className="absolute inset-0 bg-black/30 z-0" />
      <div className="relative z-10 w-full flex items-center justify-center">
        <div className="bg-white max-w-2xl w-full mx-auto md:p-8 p-5 rounded-2xl shadow-lg border border-[#cc3333]/10 mt-12 animate-fade-in backdrop-blur-sm bg-opacity-90">
          <h2 className="text-3xl font-bold mb-2 text-center text-[#cc3333] tracking-tight">Tạo tài khoản mới</h2>
          <p className="text-center text-gray-500 mb-7">Đăng ký để trải nghiệm mua sắm tuyệt vời cùng chúng tôi</p>
          <form className="space-y-4" onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Cột trái */}
              <div className="flex flex-col gap-4">
                <input
                  type="text"
                  name="username"
                  id="username"
                  value={formData.username}
                  onChange={handleChange}
                  className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
                  placeholder="Tên đăng nhập"
                  required
                  disabled={loading}
                />
                <input
                  type="password"
                  name="password"
                  id="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
                  placeholder="Mật khẩu"
                  required
                  disabled={loading}
                />
                <input
                  type="password"
                  name="confirmPassword"
                  id="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
                  placeholder="Xác nhận mật khẩu"
                  required
                  disabled={loading}
                />
              </div>
              {/* Cột phải */}
              <div className="flex flex-col gap-4">
                <input
                  type="text"
                  name="fullName"
                  id="fullName"
                  value={formData.fullName}
                  onChange={handleChange}
                  className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
                  placeholder="Họ và tên"
                  required
                  disabled={loading}
                />
                <input
                  type="email"
                  name="email"
                  id="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
                  placeholder="Email"
                  required
                  disabled={loading}
                />
                <input
                  type="tel"
                  name="phone"
                  id="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
                  placeholder="Số điện thoại"
                  required
                  disabled={loading}
                />
                <input
                  type="date"
                  name="dob"
                  id="dob"
                  value={formData.dob}
                  onChange={handleChange}
                  className="w-full bg-gray-50 border border-[#cc3333]/30 outline-none rounded-full py-3 px-5 text-base text-gray-700 focus:ring-2 focus:ring-[#cc3333] focus:border-[#cc3333] transition placeholder-gray-400"
                  placeholder="Ngày sinh"
                  required
                  disabled={loading}
                />
              </div>
            </div>
            {error && <p className="text-sm text-red-500 text-center">{error}</p>}
            <div className="flex items-center gap-2">
              <input id="terms" aria-describedby="terms" type="checkbox" className="w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-2 focus:ring-[#cc3333]" required disabled={loading} />
              <label htmlFor="terms" className="font-light text-gray-500 text-sm">Tôi đồng ý với <a className="font-medium text-[#cc3333] hover:underline" href="#">Điều khoản và Dịch vụ</a></label>
            </div>
            <button type="submit" className="w-full text-white bg-[#cc3333] hover:bg-[#b82d2d] focus:ring-4 focus:outline-none focus:ring-[#f05252] font-medium rounded-lg text-sm px-5 py-2.5 text-center disabled:opacity-60" disabled={loading}>
              {loading ? 'Đang tạo...' : 'Tạo tài khoản'}
            </button>
          </form>
          <p className="text-sm font-light text-gray-500 text-center mt-6">
            Đã có tài khoản? <a href="/login" className="font-medium text-[#cc3333] hover:underline">Đăng nhập ngay</a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterForm; 