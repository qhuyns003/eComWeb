import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import HeaderAdmin from './HeaderAdmin';
import SendGlobalNotification from './SendGlobalNotification';
import { sendNotification, getShopInfoByUserId, getMyInfo } from '../../api/api';
import ShopProducts from './ShopProducts';
import EditProduct from './EditProduct';
import AddProduct from './AddProduct';
import { useAppSelector } from '../../store/hooks';
import { selectUser } from '../../store/features/userSlice';
import { useAuth } from '../../contexts/AuthContext';

interface MenuItem {
  id: string;
  name: string;
  icon: React.ReactNode;
  badge?: string;
  isPro?: boolean;
}

const ShopAdmin: React.FC = () => {
  // ...existing code...
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [selectedMenuId, setSelectedMenuId] = useState('dashboard');
  const [showNotificationDropdown, setShowNotificationDropdown] = useState(false);
  const [notificationOption, setNotificationOption] = useState<string | null>(null);
  const user = useAppSelector(selectUser);
  const { user: keycloakUser, isAuthenticated } = useAuth();
  
  // State để lưu userId từ API
  const [userId, setUserId] = useState<string | null>(null);
  const [loadingUser, setLoadingUser] = useState(true);
  
  // Lấy user ID từ API khi component mount
  useEffect(() => {
    const fetchUserInfo = async () => {
      if (!isAuthenticated) return;
      
      try {
        setLoadingUser(true);
        
        // Thử lấy từ shop info trước (có thể có userId)
        const shopResponse = await getShopInfoByUserId();
        const shop = shopResponse.data?.result;
        
        if (shop?.userId) {
          // Shop có userId
          setUserId(shop.userId);
        } else {
          // Fallback: lấy từ user info API
          const userResponse = await getMyInfo();
          const user = userResponse.data?.result;
          if (user?.id) {
            setUserId(user.id);
          }
        }
      } catch (error) {
        console.error('Lỗi lấy thông tin user:', error);
      } finally {
        setLoadingUser(false);
      }
    };

    fetchUserInfo();
  }, [isAuthenticated]);
  const [editingProductId, setEditingProductId] = useState<string | null>(null);
  const [showAddProduct, setShowAddProduct] = useState(false);
  const navigate = useNavigate();

  const menuItems: MenuItem[] = [
    {
      id: 'dashboard',
      name: 'Tổng quan',
      icon: (
        <svg className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-[#cc3333]" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 22 21">
          <path d="M16.975 11H10V4.025a1 1 0 0 0-1.066-.998 8.5 8.5 0 1 0 9.039 9.039.999.999 0 0 0-1-1.066h.002Z"/>
          <path d="M12.5 0c-.157 0-.311.01-.565.027A1 1 0 0 0 11 1.02V10h8.975a1 1 0 0 0 1-.935c.013-.188.028-.374.028-.565A8.51 8.51 0 0 0 12.5 0Z"/>
        </svg>
      )
    },
    {
      id: 'orders',
      name: 'Đơn hàng',
      icon: (
        <svg className="shrink-0 w-5 h-5 text-gray-500 transition duration-75 group-hover:text-[#cc3333]" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 18">
          <path d="M6.143 0H1.857A1.857 1.857 0 0 0 0 1.857v4.286C0 7.169.831 8 1.857 8h4.286A1.857 1.857 0 0 0 8 6.143V1.857A1.857 1.857 0 0 0 6.143 0Zm10 0h-4.286A1.857 1.857 0 0 0 10 1.857v4.286C10 7.169 10.831 8 11.857 8h4.286A1.857 1.857 0 0 0 18 6.143V1.857A1.857 1.857 0 0 0 16.143 0Zm-10 10H1.857A1.857 1.857 0 0 0 0 11.857v4.286C0 17.169.831 18 1.857 18h4.286A1.857 1.857 0 0 0 8 16.143v-4.286A1.857 1.857 0 0 0 6.143 10Zm10 0h-4.286A1.857 1.857 0 0 0 10 11.857v4.286c0 1.026.831 1.857 1.857 1.857h4.286A1.857 1.857 0 0 0 18 16.143v-4.286A1.857 1.857 0 0 0 16.143 10Z"/>
        </svg>
      ),
      badge: '3'
    },
    {
      id: 'products',
      name: 'Sản phẩm',
      icon: (
        <svg className="shrink-0 w-5 h-5 text-gray-500 transition duration-75 group-hover:text-[#cc3333]" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 18 20">
          <path d="M17 5.923A1 1 0 0 0 16 5h-3V4a4 4 0 1 0-8 0v1H2a1 1 0 0 0-1 .923L.086 17.846A2 2 0 0 0 2.08 20h13.84a2 2 0 0 0 1.994-2.153L17 5.923ZM7 9a1 1 0 0 1-2 0V7h2v2Zm0-5a2 2 0 1 1 4 0v1H7V4Zm6 5a1 1 0 1 1-2 0V7h2v2Z"/>
        </svg>
      )
    },
    {
      id: 'customers',
      name: 'Khách hàng',
      icon: (
        <svg className="shrink-0 w-5 h-5 text-gray-500 transition duration-75 group-hover:text-[#cc3333]" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 18">
          <path d="M14 2a3.963 3.963 0 0 0-1.4.267 6.439 6.439 0 0 1-1.331 6.638A4 4 0 1 0 14 2Zm1 9h-1.264A6.957 6.957 0 0 1 15 15v2a2.97 2.97 0 0 1-.184 1H19a1 1 0 0 0 1-1v-1a5.006 5.006 0 0 0-5-5ZM6.5 9a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9ZM8 10H5a5.006 5.006 0 0 0-5 5v2a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-2a5.006 5.006 0 0 0-5-5Z"/>
        </svg>
      )
    },
    {
      id: 'analytics',
      name: 'Thống kê',
      icon: (
        <svg className="shrink-0 w-5 h-5 text-gray-500 transition duration-75 group-hover:text-[#cc3333]" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
          <path d="m17.418 3.623-.018-.008a6.713 6.713 0 0 0-2.4-.569V2h1a1 1 0 1 0 0-2h-2a1 1 0 0 0-1 1v2H9.89A6.977 6.977 0 0 1 12 8v5h-2V8A5 5 0 1 0 0 8v6a1 1 0 0 0 1 1h8v4a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-4h6a1 1 0 0 0 1-1V8a5 5 0 0 0-2.582-4.377ZM6 12H4a1 1 0 0 1 0-2h2a1 1 0 0 1 0 2Z"/>
        </svg>
      )
    },
    {
      id: 'settings',
      name: 'Cài đặt',
      icon: (
        <svg className="shrink-0 w-5 h-5 text-gray-500 transition duration-75 group-hover:text-[#cc3333]" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
          <path d="M5 5V.13a2.96 2.96 0 0 0-1.293.749L.879 3.707A2.96 2.96 0 0 0 .13 5H5Z"/>
          <path d="M6.737 11.061a2.961 2.961 0 0 1 .81-1.515l6.117-6.116A4.839 4.839 0 0 1 16 2.141V2a1.97 1.97 0 0 0-1.933-2H7v5a2 2 0 0 1-2 2H0v11a1.969 1.969 0 0 0 1.933 2h12.134A1.97 1.97 0 0 0 16 18v-3.093l-1.546 1.546c-.413.413-.94.695-1.513.81l-3.4.679a2.947 2.947 0 0 1-1.85-.227 2.96 2.96 0 0 1-1.635-3.257l.681-3.397Z"/>
          <path d="M8.961 16a.93.93 0 0 0 .189-.019l3.4-.679a.961.961 0 0 0 .49-.263l6.118-6.117a2.884 2.884 0 0 0-4.079-4.078l-6.117 6.117a.96.96 0 0 0-.263.491l-.679 3.4A.961.961 0 0 0 8.961 16Zm7.477-9.8a.958.958 0 0 1 .68-.281.961.961 0 0 1 .682 1.644l-.315.315-1.36-1.36.313-.318Zm-5.911 5.911 4.236-4.236 1.359 1.359-4.236 4.237-1.7.339.341-1.699Z"/>
        </svg>
      )
    }
  ];

  const handleEditProduct = (productId: string) => {
    setEditingProductId(productId);
  };

  const handleCancelEdit = () => {
    setEditingProductId(null);
  };

  const handleAddProduct = () => {
    setShowAddProduct(true);
  };

  const handleCancelAdd = () => {
    setShowAddProduct(false);
  };

  const handleSaveSuccess = () => {
    setShowAddProduct(false);
    setEditingProductId(null);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <HeaderAdmin />
      <div className="flex">
        {/* Sidebar hiện đại, luôn sát đáy, gradient, shadow, bo góc lớn */}
  <aside className={`relative transition-all duration-300 ${isCollapsed ? 'w-20' : 'w-64'} h-screen bg-gradient-to-b from-[#cc3333] via-[#faeaea] to-white shadow-2xl border-4 border-white rounded-3xl flex flex-col`}>
          <button
            className="absolute top-3 -right-3 z-10 bg-white border rounded-full shadow p-1 hover:bg-gray-100 transition"
            onClick={() => setIsCollapsed(v => !v)}
            aria-label="Toggle sidebar"
            type="button"
          >
            {isCollapsed ? (
              <svg width="20" height="20" fill="none"><path d="M7 5l5 5-5 5" stroke="#cc3333" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
            ) : (
              <svg width="20" height="20" fill="none"><path d="M13 5l-5 5 5 5" stroke="#cc3333" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
            )}
          </button>
          {/* Menu chính, Phát thông báo cuối cùng */}
          <div className="flex-1 px-3 pt-6 pb-6 overflow-y-auto">
            <ul className="space-y-2 font-medium">
              {menuItems.map((item) => (
                <li key={item.id} className="relative">
                  <button
                    onClick={() => setSelectedMenuId(item.id)}
                    className={`w-full flex items-center gap-3 px-4 py-3 text-gray-900 rounded-2xl group transition-all duration-300 shadow-sm hover:bg-[#ffeaea] hover:scale-[1.03] ${selectedMenuId === item.id ? 'bg-white font-bold text-[#cc3333] border-l-4 border-[#cc3333] shadow-lg' : ''}`}
                  >
                    {item.icon}
                    <span className={`${isCollapsed ? 'hidden' : 'inline'} whitespace-nowrap`}>{item.name}</span>
                    {item.badge && !isCollapsed && (
                      <span className="inline-flex items-center justify-center w-6 h-6 text-xs font-medium text-white bg-[#cc3333] rounded-full shadow">{item.badge}</span>
                    )}
                    {item.isPro && !isCollapsed && (
                      <span className="inline-flex items-center justify-center px-2 text-xs font-medium text-[#cc3333] bg-[#faeaea] rounded-full">Pro</span>
                    )}
                  </button>
                </li>
              ))}
              {/* Chỉ hiện menu Phát thông báo nếu là admin */}
              {user?.roles?.some((role: any) => role.id === 'ADMIN') && (
                <li className="relative">
                  <button
                    onClick={() => {
                      setShowNotificationDropdown(v => !v);
                    }}
                    className={`w-full flex items-center gap-3 px-4 py-3 text-gray-900 rounded-2xl group transition-all duration-300 shadow-sm hover:bg-[#ffeaea] hover:scale-[1.03] ${selectedMenuId === 'notification' ? 'bg-white font-bold text-[#cc3333] border-l-4 border-[#cc3333] shadow-lg' : ''}`}
                  >
                    <svg className="w-5 h-5 text-gray-500 transition duration-75 group-hover:text-[#cc3333]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" /></svg>
                    <span className={`${isCollapsed ? 'hidden' : 'inline'} whitespace-nowrap`}>Phát thông báo</span>
                    <svg className={`ml-auto w-4 h-4 transition-transform duration-300 ${showNotificationDropdown ? 'rotate-180' : ''}`} fill="none" stroke="#cc3333" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" /></svg>
                  </button>
                  <div className={`overflow-hidden transition-all duration-300 ${showNotificationDropdown ? 'max-h-40 opacity-100' : 'max-h-0 opacity-0'} bg-white rounded-lg shadow border border-gray-200`} style={{marginBottom: showNotificationDropdown ? '1rem' : '0'}}>
                    <ul>
                      <li>
                        <button
                          className="w-full text-left px-4 py-3 hover:bg-[#faeaea] text-gray-700 font-semibold rounded-b-lg transition"
                          onClick={() => {
                            setSelectedMenuId('notification');
                            setNotificationOption('global');
                          }}
                        >Phát thông báo chung</button>
                      </li>
                      {/* Có thể thêm các option khác ở đây */}
                    </ul>
                  </div>
                </li>
              )}
            </ul>
          </div>
        </aside>
        {/* Main Content */}
        <main className="flex-1 p-4">
          {selectedMenuId === 'products' ? (
            showAddProduct ? (
              <AddProduct
                onSaveSuccess={handleSaveSuccess}
                onCancel={handleCancelAdd}
              />
            ) : editingProductId !== null ? (
              <EditProduct
                productId={editingProductId}
                onSaveSuccess={handleCancelEdit}
                onCancel={handleCancelEdit}
              />
            ) : (
              loadingUser ? (
                <div className="text-center py-8">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                  <p className="mt-2 text-gray-600">Đang tải thông tin user...</p>
                </div>
              ) : userId ? (
                <ShopProducts userId={userId} onEdit={handleEditProduct} onAdd={handleAddProduct} />
              ) : (
                <div className="text-center py-8">
                  <p className="text-red-600">Không tìm thấy thông tin user. Vui lòng liên hệ admin.</p>
                </div>
              )
            )
          ) : selectedMenuId === 'notification' && notificationOption === 'global' ? (
            <SendGlobalNotification />
          ) : (
            <div className="p-6 border-2 border-gray-200 border-dashed rounded-lg bg-white text-gray-400 text-xl text-center">
              Chọn một mục trong menu để quản lý
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default ShopAdmin; 