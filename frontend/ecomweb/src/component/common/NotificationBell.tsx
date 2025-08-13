import React, { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import { useAppSelector } from "../../store/hooks";
import { selectUser } from "../../store/features/userSlice";
import { fetchUserNotifications, markNotificationAsRead } from "../../api/api";

interface Notification {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  seen: boolean;
}

const NotificationBell: React.FC = () => {
  const user = useAppSelector(selectUser);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const stompClient = useRef<Client | null>(null);

  useEffect(() => {
    if (!user) return;
    // Fetch notifications on mount
    fetchUserNotifications(user.id)
      .then(data => {
        setNotifications(data);
        setUnreadCount(data.filter((n: Notification) => !n.seen).length);
      });
    // Lấy token từ localStorage hoặc redux (tùy app của bạn)
    const token = localStorage.getItem("token");
    // Truyền token qua query param khi tạo SockJS
    const socket = new SockJS(`http://localhost:8080/ws${token ? `?token=${token}` : ''}`);
    const client = new Client({
      webSocketFactory: () => socket as any,
      // Không cần connectHeaders cho handshake với SockJS, chỉ cần query param
      onConnect: () => {
        client.subscribe("/user/queue/notifications", (message) => {
          const notification: Notification = JSON.parse(message.body);
          setNotifications(prev => [notification, ...prev]);
          setUnreadCount(prev => prev + 1);
        });
      },
    });
    client.activate();
    stompClient.current = client;
    return () => {
      client.deactivate();
    };
  }, [user]);

  const handleReadNotification = (id: string) => {
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, seen: true } : n));
    setUnreadCount(prev => prev - 1);
  markNotificationAsRead(id);
  };

  return (
    <div className="relative">
      <button
        className="relative focus:outline-none"
        onClick={() => setShowDropdown(v => !v)}
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
                  key={n.id}
                  className={`p-4 border-b last:border-b-0 cursor-pointer hover:bg-gray-100 ${!n.seen ? 'bg-blue-50' : ''}`}
                  onClick={() => handleReadNotification(n.id)}
                >
                  <div className="font-semibold">{n.title}</div>
                  <div className="text-sm text-gray-600">{n.content}</div>
                  <div className="text-xs text-gray-400">{new Date(n.createdAt).toLocaleString()}</div>
                </li>
              ))
            )}
          </ul>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
