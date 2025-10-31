import React, { useState, useEffect } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useAppSelector } from '../../store/hooks';
import { selectUser } from '../../store/features/userSlice';
import ShopActionButton from './ShopActionButton';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

// Giả sử có API updateUser, bạn cần implement ở api.ts
import { updateUser, getShopInfoByUserId, getMyInfo } from '../../api/api';
import UserAddressEdit from './UserAddressEdit';
import Header from '../layout/Header';
import Footer from '../layout/Footer';

const UserProfileEdit: React.FC = () => {
  const user = useAppSelector(selectUser);
  const { isSeller, user: keycloakUser, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  
  // Ưu tiên Keycloak user, fallback Redux user
  const currentUser = keycloakUser || user;
  
  const [form, setForm] = useState({
    fullName: '',
    dob: '',
    email: '',
    phone: '',
    username: '',
  });
  
  const [loading, setLoading] = useState(false);
  const [userLoading, setUserLoading] = useState(true);

  // Lấy thông tin user chi tiết từ API
  useEffect(() => {
    const fetchUserInfo = async () => {
      if (!isAuthenticated) return;
      
      try {
        setUserLoading(true);
        
        // Thử lấy từ Keycloak token trước
        if (keycloakUser) {
          setForm({
            fullName: keycloakUser.name || keycloakUser.fullName || '',
            dob: keycloakUser.dob || '',
            email: keycloakUser.email || '',
            phone: keycloakUser.phone || '',
            username: keycloakUser.preferred_username || keycloakUser.username || '',
          });
        }
        
        // Sau đó lấy thông tin chi tiết từ backend (nếu có)
        try {
          const response = await getMyInfo();
          const userInfo = response.data?.result;
          
          if (userInfo) {
            setForm(prev => ({
              fullName: userInfo.fullName || prev.fullName,
              dob: userInfo.dob || prev.dob,
              email: userInfo.email || prev.email,
              phone: userInfo.phone || prev.phone,
              username: userInfo.username || prev.username,
            }));
          }
        } catch (error) {
          console.log('Không lấy được thông tin từ backend, dùng Keycloak info');
        }
        
      } catch (error) {
        console.error('Lỗi lấy thông tin user:', error);
      } finally {
        setUserLoading(false);
      }
    };

    fetchUserInfo();
  }, [isAuthenticated, keycloakUser]);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  // Đổi mật khẩu
  const [showChangePw, setShowChangePw] = useState(false);
  const [pwForm, setPwForm] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [pwError, setPwError] = useState('');
  const [pwSuccess, setPwSuccess] = useState('');
  const [pwLoading, setPwLoading] = useState(false);

  const handlePwChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPwForm({ ...pwForm, [e.target.name]: e.target.value });
  };
  const handleOpenPwModal = () => {
    setShowChangePw(true);
    setPwForm({ oldPassword: '', newPassword: '', confirmPassword: '' });
    setPwError('');
    setPwSuccess('');
  };
  const handleClosePwModal = () => {
    setShowChangePw(false);
    setPwError('');
    setPwSuccess('');
  };
  const handleSubmitPw = async (e: React.FormEvent) => {
    e.preventDefault();
    setPwError('');
    setPwSuccess('');
    if (!pwForm.oldPassword || !pwForm.newPassword || !pwForm.confirmPassword) {
      setPwError('Vui lòng nhập đầy đủ thông tin');
      return;
    }
    if (pwForm.newPassword !== pwForm.confirmPassword) {
      setPwError('Mật khẩu mới không khớp');
      return;
    }
    setPwLoading(true);
    try {
      await updateUser({
        ...form,
        password: pwForm.newPassword,
        oldPassword: pwForm.oldPassword,
        repeatPassword: pwForm.confirmPassword
      });
      toast.success('Đổi mật khẩu thành công!');
      setShowChangePw(false);
    } catch (err: any) {
      const msg = err?.response?.data?.message || err?.message || 'Có lỗi xảy ra';
      toast.error(msg);
    } finally {
      setPwLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess(false);
    try {
      await updateUser(form); // Cần implement API này
      setSuccess(true);
      toast.success('Cập nhật thành công!');
    } catch (err: any) {
      const msg = err?.response?.data?.message || err?.message || 'Có lỗi xảy ra';
      setError(msg);
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Header />
      {userLoading ? (
        <div className="max-w-5xl mx-auto mt-8">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Đang tải thông tin cá nhân...</p>
          </div>
        </div>
      ) : (
        <>
          <div className="max-w-5xl mx-auto mt-8">
        <div className="pl-2">
          <button
            type="button"
            onClick={() => navigate('/')}
            className="flex items-center text-base text-gray-700 hover:text-[#cc3333] font-medium mb-4 transition-colors"
            style={{ outline: 'none' }}
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Quay lại
          </button>
        </div>
        {/* ...existing code... */}
      </div>
      <div className="max-w-5xl mx-auto mt-10 flex flex-col md:flex-row gap-8">
        {/* Left: User Info */}
        <div className="flex-1 min-w-[320px]">
          <div className="bg-white rounded-2xl shadow-xl p-8 flex flex-col gap-6">
            <h2 className="text-2xl font-bold text-[#cc3333] mb-2 tracking-tight">Thông tin cá nhân</h2>
            <div className="flex gap-2 mb-2">
              <button type="button" onClick={handleOpenPwModal} className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-5 py-2 rounded-full shadow hover:scale-105 transition-transform font-semibold">Đổi mật khẩu</button>
              <ShopActionButton
                isSeller={isSeller}
                onShopInfo={() => {
                  navigate('/shop-info');
                }}
                onRegisterShop={() => navigate('/register-shop')}
                className="px-5 py-2 rounded-full shadow hover:scale-105 transition-transform font-semibold text-white text-base leading-tight"
              />
            </div>
            <form onSubmit={handleSubmit} className="space-y-5">
              <div>
                <label className="block font-medium mb-1">Họ tên</label>
                <input name="fullName" value={form.fullName} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-[#cc3333]" />
              </div>
              <div>
                <label className="block font-medium mb-1">Ngày sinh</label>
                <input name="dob" type="date" value={form.dob} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-[#cc3333]" />
              </div>
              <div>
                <label className="block font-medium mb-1">Email</label>
                <input name="email" type="email" value={form.email} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-[#cc3333]" />
              </div>
              <div>
                <label className="block font-medium mb-1">Số điện thoại</label>
                <input name="phone" value={form.phone} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-[#cc3333]" />
              </div>
              <div>
                <label className="block font-medium mb-1">Tên đăng nhập</label>
                <input name="username" value={form.username} onChange={handleChange} className="border border-gray-200 bg-gray-100 rounded-lg px-3 py-2 w-full" disabled />
              </div>
              <button type="submit" className="bg-gradient-to-r from-[#cc3333] to-[#b82d2d] text-white px-6 py-2 rounded-full font-semibold hover:scale-105 transition-transform w-full shadow" disabled={loading}>
                {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
              </button>
              {/* Toast sẽ hiển thị qua react-toastify, không hiện inline */}
            </form>
          </div>
        </div>
        {/* Right: Address Edit */}
        <div className="flex-1 min-w-[340px]">
          <div className="bg-white rounded-2xl shadow-xl p-8">
            <UserAddressEdit />
          </div>
        </div>
        {/* Modal đổi mật khẩu */}
        {showChangePw && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
            <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md relative animate-fadeIn">
              <button onClick={handleClosePwModal} className="absolute top-3 right-3 text-gray-400 hover:text-black text-2xl font-bold">&times;</button>
              <h4 className="text-xl font-bold mb-6 text-[#cc3333] tracking-tight">Đổi mật khẩu</h4>
              <form onSubmit={handleSubmitPw} className="space-y-5">
                <div>
                  <label className="block font-medium mb-1">Mật khẩu cũ</label>
                  <input name="oldPassword" type="password" value={pwForm.oldPassword} onChange={handlePwChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block font-medium mb-1">Mật khẩu mới</label>
                  <input name="newPassword" type="password" value={pwForm.newPassword} onChange={handlePwChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block font-medium mb-1">Nhập lại mật khẩu mới</label>
                  <input name="confirmPassword" type="password" value={pwForm.confirmPassword} onChange={handlePwChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                {pwError && <div className="text-red-600 mt-2">{pwError}</div>}
                <div className="flex gap-3 mt-2">
                  <button type="submit" className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-2 rounded-full font-semibold hover:scale-105 transition-transform flex-1 shadow" disabled={pwLoading}>
                    {pwLoading ? 'Đang lưu...' : 'Đổi mật khẩu'}
                  </button>
                  <button type="button" onClick={handleClosePwModal} className="bg-gray-100 text-gray-700 px-6 py-2 rounded-full font-semibold hover:bg-gray-200 transition flex-1 shadow">Hủy</button>
                </div>
              </form>
            </div>
          </div>
        )}
        <ToastContainer position="top-right" autoClose={2000} hideProgressBar={false} newestOnTop closeOnClick pauseOnFocusLoss draggable pauseOnHover theme="colored" />
        </div>
        </>
      )}
      <Footer />
    </>
  );
};

export default UserProfileEdit;
