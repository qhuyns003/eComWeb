import React, { useState } from 'react';
import { sendNotification } from '../../api/api';
import { getAllUsers } from '../../api/api';
import SockJS from 'sockjs-client/dist/sockjs';
import { Client } from '@stomp/stompjs';
import { useAppSelector } from '../../store/hooks';
import { selectUser } from '../../store/features/userSlice';
import { useAuth } from '../../contexts/AuthContext';

const SendGlobalNotification: React.FC<{ onSent?: () => void }> = ({ onSent }) => {
  const user = useAppSelector(selectUser);
  const { isAdmin } = useAuth();
  
  // Chỉ cho phép ADMIN truy cập
  if (!isAdmin) {
    return <div className="text-red-500 text-center font-bold p-8">Bạn không có quyền truy cập chức năng này.</div>;
  }
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [modal, setModal] = useState<{ type: 'success' | 'error', message: string } | null>(null);
  const [userList, setUserList] = useState<{id: string, username: string, fullName?: string}[]>([]);
  const [selectedUserIds, setSelectedUserIds] = useState<string[]>([]);
  // Lấy danh sách user hệ thống khi mount
  React.useEffect(() => {
    getAllUsers()
      .then(res => {
        const users = res.data?.result || res.data;
        setUserList(users);
      })
      .catch(() => setUserList([]));
  }, []);
  // Khởi tạo kết nối socket khi component mount
  React.useEffect(() => {
    if (!user) return;
    const token = localStorage.getItem('token');
    const socket = new SockJS(`http://localhost:8080/notifications/ws-notification${token ? `?token=${token}` : ''}`);
    const client = new Client({
      webSocketFactory: () => socket as any,
      onConnect: () => {},
    });
    client.activate();
    setStompClient(client);
    return () => {
      client.deactivate();
    };
  }, [user]);
  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
  setLoading(true);
  setError('');
  setSuccess(false);
  setModal(null);
    try {
      // Lấy danh sách user hệ thống từ userList
      const recipientIds = userList.map(u => u.id);
      // Gửi qua socket nếu có kết nối
      if (stompClient && stompClient.connected) {
        stompClient.publish({
          destination: '/ws-app/send',
          body: JSON.stringify({
            recipientId: recipientIds,
            type: 'text',
            title,
            message
          })
        });
        setSuccess(true);
        setModal({ type: 'success', message: 'Gửi thông báo thành công!' });
        if (onSent) onSent();
      } else {
        setError('Không kết nối được tới server socket!');
        setModal({ type: 'error', message: 'Không kết nối được tới server socket!' });
      }
    } catch (err) {
      setError('Gửi thông báo thất bại!');
      setModal({ type: 'error', message: 'Gửi thông báo thất bại!' });
    } finally {
      setLoading(false);
    }
  };
  return (
    <div className="max-w-lg mx-auto bg-white rounded-3xl shadow-2xl border border-[#ffeaea] p-8 flex flex-col items-center animate-fade-in">
      {modal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-30">
          <div className="bg-white rounded-2xl shadow-2xl p-8 min-w-[320px] flex flex-col items-center animate-fade-in">
            <svg className={`w-10 h-10 mb-4 ${modal.type === 'success' ? 'text-green-500' : 'text-red-500'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {modal.type === 'success' ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              )}
            </svg>
            <div className="text-xl font-bold mb-2 text-center">{modal.message}</div>
            <button
              className="mt-4 px-6 py-2 bg-gradient-to-r from-[#cc3333] to-[#ff6666] text-white rounded-full font-semibold shadow hover:scale-105 transition"
              onClick={() => setModal(null)}
            >Đóng</button>
          </div>
        </div>
      )}
      <div className="w-10 h-10 flex items-center justify-center rounded-full bg-gradient-to-br from-[#cc3333] via-[#ff6666] to-[#ffb3b3] mb-2 shadow-lg">
        <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" /></svg>
      </div>
      <h2 className="text-xl font-bold text-[#cc3333] mb-4 text-center tracking-tight drop-shadow">Phát thông báo chung</h2>
  <form onSubmit={handleSend} className="w-full space-y-6">
        <div>
          <label className="block text-base font-semibold text-gray-700 mb-2">Tiêu đề</label>
          <input
            type="text"
            className="w-full px-5 py-3 border-2 border-[#ffeaea] rounded-2xl focus:outline-none focus:ring-2 focus:ring-[#cc3333] bg-[#fff7f7] text-gray-900 text-lg shadow-sm placeholder:text-gray-400"
            value={title}
            onChange={e => setTitle(e.target.value)}
            placeholder="Nhập tiêu đề thông báo..."
            required
          />
        </div>
        <div>
          <label className="block text-base font-semibold text-gray-700 mb-2">Nội dung</label>
          <textarea
            className="w-full px-5 py-3 border-2 border-[#ffeaea] rounded-2xl focus:outline-none focus:ring-2 focus:ring-[#cc3333] bg-[#fff7f7] text-gray-900 text-lg shadow-sm min-h-[220px] max-h-[400px] resize-vertical placeholder:text-gray-400"
            value={message}
            onChange={e => setMessage(e.target.value)}
            placeholder="Nhập nội dung thông báo..."
            required
          />
        </div>
        <button
          type="submit"
          className="w-full py-3 bg-gradient-to-r from-[#cc3333] via-[#ff6666] to-[#ffb3b3] text-white font-bold rounded-2xl shadow-xl hover:scale-[1.04] hover:shadow-2xl transition-transform duration-200"
          disabled={loading}
        >
          {loading ? (
            <span className="flex items-center justify-center gap-2">
              <svg className="animate-spin h-5 w-5 text-white" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" /><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z" /></svg>
              Đang gửi...
            </span>
          ) : (
            'Gửi thông báo'
          )}
        </button>
        {error && (
          <div className="text-red-500 text-center mt-2 font-semibold">{error}</div>
        )}
        {success && (
          <div className="text-green-500 text-center mt-2 font-semibold">{success}</div>
        )}
      </form>
    </div>
  );

}
export default SendGlobalNotification;
