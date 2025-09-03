// ...existing code...
import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import { useAppSelector } from "../../store/hooks";
import { selectUser } from "../../store/features/userSlice";
import { fetchUserNotifications, markNotificationAsRead } from "../../api/api";

interface NotificationKeyResponse {
  userId: string;
  createdAt: string;
  notificationId: string;
}

interface NotificationResponse {
  key: NotificationKeyResponse;
  type: string;
  title: string;
  message: string;
  status: string;
  seen?: boolean; // nếu có
}

interface NotificationBellProps {
  showDropdown: boolean;
  onToggle: () => void;
  unreadCount: number;
  setUnreadCount: React.Dispatch<React.SetStateAction<number>>;
}

const NotificationBell: React.FC<NotificationBellProps> = ({ showDropdown, onToggle, unreadCount, setUnreadCount }) => {
  const user = useAppSelector(selectUser);
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  // unreadCount và setUnreadCount được truyền từ Header xuống, không dùng state cục bộ nữa
  const stompClient = useRef<Client | null>(null);
  const [openId, setOpenId] = useState<string | null>(null);
  const [modalNotification, setModalNotification] = useState<NotificationResponse | null>(null);

  useEffect(() => {
    if (!user) return;
    // Fetch notifications on mount
    fetchUserNotifications(user.id)
      .then(data => {
        // Nếu backend trả về { result: [...] }
        const notiList = data?.result || [];
        setNotifications(notiList);
  setUnreadCount(notiList.filter((n: NotificationResponse) => n.status === "UNREAD").length);
      });
    // Lấy token từ localStorage hoặc redux (tùy app của bạn)
    const token = localStorage.getItem("token");
    // Truyền token qua query param khi tạo SockJS
    const socket = new SockJS(`http://localhost:8080/notifications/ws-notification${token ? `?token=${token}` : ''}`);
    const client = new Client({
      webSocketFactory: () => socket as any,
      onConnect: () => {
        client.subscribe("/user/queue/notifications", (message) => {
          // Nếu backend trả về { result: {...} }
          let notification: NotificationResponse;
          try {
            const body = JSON.parse(message.body);
            notification = body?.result || body;
          } catch {
            notification = message.body as any;
          }
          setNotifications(prev => [notification, ...prev]);
          setUnreadCount(prev => prev + (notification.status === "UNREAD" ? 1 : 0));
        });
      },
    });
    client.activate();
    stompClient.current = client;
    return () => {
      client.deactivate();
    };
  }, [user]);

  const handleOpenNotification = (notification: NotificationResponse) => {
    setModalNotification(notification);
    // Gửi đúng requestBody cho NotificationKeyRequest
    if (stompClient.current && stompClient.current.connected) {
      stompClient.current.publish({
        destination: "/ws-app/read",
        body: JSON.stringify({
          userId: notification.key.userId,
          createdAt: notification.key.createdAt,
          notificationId: notification.key.notificationId,
        }),
      });
    } else {
      console.warn("STOMP client chưa kết nối hoặc chưa sẵn sàng");
    }
    setNotifications(prev => prev.map(n =>
      n.key.notificationId === notification.key.notificationId
        ? { ...n, status: "READ" }
        : n
    ));
    setUnreadCount(prev => prev - (notification.status === "UNREAD" ? 1 : 0));
  };

  return (
    <div className="relative">
      <button
        className="relative focus:outline-none"
        onClick={onToggle}
        aria-label="Thông báo"
      >
        <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        {unreadCount > 0 && (
          <span className="absolute top-0 right-0 inline-flex items-center justify-center px-1 py-0.5 text-xs font-bold leading-none text-white bg-red-600 rounded-full">
            {unreadCount}
          </span>
        )}
      </button>
      {showDropdown && (
        <div className="absolute right-0 mt-2 w-80 bg-white border rounded-lg shadow-lg z-50">
          <div className="p-4 border-b font-bold">Thông báo</div>
          <ul className="max-h-96 overflow-y-auto">
            {notifications.length === 0 ? (
              <li className="p-4 text-gray-500 text-center">Không có thông báo mới</li>
            ) : (
              notifications.map((n) => (
                <li
                  key={n.key.notificationId}
                  className={`flex items-center justify-between p-3 border-b last:border-b-0 cursor-pointer transition-colors duration-150 ${n.status === 'UNREAD' ? 'bg-blue-50 hover:bg-blue-100' : 'hover:bg-gray-100'}`}
                  onClick={() => handleOpenNotification(n)}
                >
                  <div>
                    <div className={`font-semibold truncate ${n.status === 'UNREAD' ? 'text-blue-700' : 'text-gray-800'}`}>{n.title}</div>
                    <div className="text-xs text-gray-400 mt-1">{n.key.createdAt ? new Date(n.key.createdAt).toLocaleString() : ''}</div>
                  </div>
                  {n.status === 'UNREAD' && <span className="ml-2 w-2 h-2 bg-blue-500 rounded-full inline-block"></span>}
                </li>
              ))
            )}
          </ul>
        </div>
      )}

      {/* Modal hiển thị chi tiết thông báo */}
      {modalNotification && (
        <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-30">
          <div className="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md animate-fade-in relative">
            <button
              className="absolute top-2 right-2 text-gray-400 hover:text-red-500 text-xl font-bold focus:outline-none"
              onClick={() => setModalNotification(null)}
              aria-label="Đóng"
            >
              ×
            </button>
            <div className="mb-2 text-lg font-bold text-blue-700 flex items-center gap-2">
              <svg className="w-6 h-6 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" /></svg>
              {modalNotification.title}
            </div>
            <div className="mb-4 text-gray-700 whitespace-pre-line">{modalNotification.message}</div>
            <div className="flex justify-between text-xs text-gray-400">
              <span>{modalNotification.key.createdAt ? new Date(modalNotification.key.createdAt).toLocaleString() : ''}</span>
             
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
