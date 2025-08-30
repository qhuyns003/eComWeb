import React from "react";
import { searchProductByImage } from '../../api/api';
import UserMenu from "../homepage/UserMenu";
import NotificationBell from "../common/NotificationBell";
import ChatBox from "../chat/ChatBox";
import { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import { fetchUserRooms, fetchRoomById } from "../../api/api";
import { useNavigate, useLocation } from "react-router-dom";
import { useAppSelector } from "../../store/hooks";
import { selectUser } from "../../store/features/userSlice";

const Header: React.FC = () => {
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const [searchValue, setSearchValue] = React.useState("");
  const user = useAppSelector(selectUser);
  const [showChatDropdown, setShowChatDropdown] = useState(false);
  const [showNotificationDropdown, setShowNotificationDropdown] = useState(false);
  const [chatRooms, setChatRooms] = useState<any[]>([]);
  const [selectedRoomId, setSelectedRoomId] = useState<string | null>(null);
  // Số phòng chat có tin nhắn chưa đọc
  const unreadChatRooms = chatRooms.filter(room => room.seen === false).length;

  // Kết nối WebSocket để cập nhật danh sách phòng chat realtime
  const stompClient = useRef<Client | null>(null);

  // Hàm lấy danh sách phòng chat kèm chi tiết tên phòng
  const fetchRoomsWithDetail = async () => {
    try {
      const res = await fetchUserRooms();
      const result = res.data?.result;
      if (!result) {
        setChatRooms([]);
        return;
      }
      // Ưu tiên lấy roomName từ user_rooms, chỉ fallback sang fetchRoomById nếu thiếu
      const roomDetails = await Promise.all(
        result.map(async (item: any) => {
          const roomId = item.key.roomId;
          const name = item.roomName || item.room_name;
          if (name) {
            return {
              ...item,
              roomId,
              lastMessageAt: item.key?.lastMessageAt,
              name,
            };
          } else {
            const detail = await fetchRoomById(roomId);
            return {
              ...item,
              roomId,
              lastMessageAt: item.key?.lastMessageAt,
              name: detail.data?.result?.name,
            };
          }
        })
      );
      setChatRooms(roomDetails);
    } catch {
      setChatRooms([]);
    }
  };

  useEffect(() => {
    if (!user) return;
    fetchRoomsWithDetail();

    // Kết nối WebSocket
    const token = localStorage.getItem("token");
    const socket = new SockJS(`http://localhost:8080/ws${token ? `?token=${token}` : ''}`);
    const client = new Client({
      webSocketFactory: () => socket as any,
      onConnect: () => {
        // Lắng nghe cập nhật danh sách phòng chat
        client.subscribe("/user/queue/chat-rooms", (message) => {
          fetchRoomsWithDetail();
        });
        // Lắng nghe thông báo chat-notification (cập nhật trạng thái đã đọc)
        client.subscribe("/user/queue/chat-notification", (message) => {
          fetchRoomsWithDetail();
        });
      },
    });
    client.activate();
    stompClient.current = client;
    return () => {
      client.deactivate();
    };
  }, [user]);
  const navigate = useNavigate();
  const location = useLocation();
  const fileInputRef = React.useRef<HTMLInputElement>(null);
  const [loadingImageSearch, setLoadingImageSearch] = React.useState(false);

  const handleImageChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setLoadingImageSearch(true);
    try {
      const res = await searchProductByImage(file, 0, 8);
      navigate('/search', { state: { imageSearchResult: res.data.result } });
    } catch (err) {
      alert('Lỗi tìm kiếm bằng hình ảnh');
    }
    setLoadingImageSearch(false);
  };

  return (
    <nav className="h-[70px] sticky top-0 w-full px-6 md:px-16 lg:px-24 xl:px-32 flex items-center justify-between z-50 bg-gradient-to-r from-[#cc3333] to-pink-500 transition-all shadow">
      {/* Logo text graffiti style */}
      <button
        type="button"
        className="bg-transparent border-none outline-none p-0 m-0"
        onClick={() => navigate('/')}
        aria-label="Go to homepage"
      >
        <span
          style={{
            fontFamily: 'Permanent Marker, cursive',
            fontSize: '2rem',
            color: '#fff',
            letterSpacing: '2px',
            textShadow: '2px 2px 0 #cc3333, 4px 4px 0 #b82d2d',
            lineHeight: 1
          }}
        >
          Easier
        </span>
      </button>


      <form
        className="flex items-center border pl-4 gap-2 bg-white border-gray-500/30 h-[46px] rounded-full overflow-hidden max-w-md w-full"
        onSubmit={e => {
          e.preventDefault();
          if (searchValue.trim()) {
            navigate(`/search?search=${encodeURIComponent(searchValue.trim())}`);
          }
        }}
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 30 30" fill="#6B7280">
          <path d="M13 3C7.489 3 3 7.489 3 13s4.489 10 10 10a9.95 9.95 0 0 0 6.322-2.264l5.971 5.971a1 1 0 1 0 1.414-1.414l-5.97-5.97A9.95 9.95 0 0 0 23 13c0-5.511-4.489-10-10-10m0 2c4.43 0 8 3.57 8 8s-3.57 8-8 8-8-3.57-8-8 3.57-8 8-8"/>
        </svg>
        <input
          type="text"
          className="w-full h-full outline-none text-sm text-gray-500"
          placeholder="Tìm kiếm sản phẩm..."
          value={searchValue}
          onChange={e => setSearchValue(e.target.value)}
        />
        <input
          type="file"
          accept="image/*"
          style={{ display: "none" }}
          ref={fileInputRef}
          onChange={handleImageChange}
        />
        <button
          type="button"
          className="bg-[#cc3333] h-9 px-4 rounded-full text-white flex items-center justify-center hover:bg-[#b82d2d] transition mr-[5px]"
          style={{ minWidth: 0 }}
          onClick={() => fileInputRef.current?.click()}
          title="Tìm kiếm bằng hình ảnh"
          disabled={loadingImageSearch}
        >
          {loadingImageSearch ? (
            <svg className="animate-spin" width="18" height="18" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"></path></svg>
          ) : (
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h3l2-3h6l2 3h3a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>
          )}
        </button>
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
      <div className="flex gap-3 items-center">
        {user && (
          <NotificationBell
            showDropdown={showNotificationDropdown}
            onToggle={() => {
              setShowNotificationDropdown(v => {
                if (!v) setShowChatDropdown(false);
                return !v;
              });
            }}
          />
        )}
        {/* Nút chat */}
        {user && (
          <div className="relative">
            <button
              className="relative focus:outline-none"
              aria-label="Nhắn tin"
              onClick={() => setShowChatDropdown(v => {
                if (!v) setShowNotificationDropdown(false);
                return !v;
              })}
            >
              {/* Icon chat */}
              <svg className="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M21 12c0 4.418-4.03 8-9 8a9.77 9.77 0 01-4-.8L3 20l.8-4A8.96 8.96 0 013 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              {unreadChatRooms > 0 && (
                <span className="absolute top-0 right-0 inline-flex items-center justify-center px-1 py-0.5 text-xs font-bold leading-none text-white bg-red-600 rounded-full">
                  {unreadChatRooms}
                </span>
              )}
            </button>
            {showChatDropdown && (
              <div className="absolute right-0 mt-2 w-80 bg-white border rounded-lg shadow-lg z-50">
                <div className="p-4 border-b font-bold">Phòng chat</div>
                <ul className="max-h-96 overflow-y-auto">
                  {chatRooms.length === 0 ? (
                    <li className="p-4 text-gray-500 text-center">Không có phòng chat</li>
                  ) : (
                    chatRooms.map((room, idx) => (
                      <li
                        key={room.roomId + '-' + idx}
                        className="p-4 border-b last:border-b-0 cursor-pointer hover:bg-gray-100"
                        onClick={() => {
                          setSelectedRoomId(room.roomId);
                          setShowChatDropdown(false);
                          // Gửi socket xác nhận đã đọc nếu phòng này chưa đọc
                          if (room.seen === false && stompClient.current && stompClient.current.connected) {
                            stompClient.current.publish({
                              destination: "/ws-app/messages/mark-read",
                              body: JSON.stringify({
                                userId: user.id,
                                roomId: room.roomId,
                                lastMessageAt: room.lastMessageAt
                              })
                            });
                          }
                        }}
                      >
                        <div className="font-semibold flex items-center">
                          {room.name || room.roomName || room.room_name || 'Phòng chat'}
                          {!room.seen && (
                            <span className="ml-2 w-2 h-2 bg-blue-500 rounded-full inline-block"></span>
                          )}
                        </div>
                        <div className="text-xs text-gray-400">{room.lastMessageAt ? new Date(room.lastMessageAt).toLocaleString() : ''}</div>
                      </li>
                    ))
                  )}
                </ul>
              </div>
            )}
          </div>
        )}
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

      {/* ChatBox nhỏ góc dưới bên phải */}
      {selectedRoomId && (
        <ChatBox 
          roomId={selectedRoomId} 
          roomName={chatRooms.find(room => room.roomId === selectedRoomId)?.name || ''} 
          onClose={() => setSelectedRoomId(null)} 
        />
      )}

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