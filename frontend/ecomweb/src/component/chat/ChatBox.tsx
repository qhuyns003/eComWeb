import React, { useEffect, useRef, useState } from "react";
import { useUserRooms } from "../../hooks/useUserRooms";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";

interface Message {
  id: string;
  sender: string;
  content: string;
  sentAt: string;
}

interface ChatBoxProps {
  roomId: string;
  onClose: () => void;
}

const ChatBox: React.FC<ChatBoxProps> = ({ roomId, onClose }) => {
  // Demo: Lấy token từ localStorage, thực tế nên lấy từ redux hoặc context
  const token = localStorage.getItem("token") || "";
  const { rooms, loading, error } = useUserRooms();
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const stompClient = useRef<Client | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Fetch messages history (replace with your API)
    fetch(`/api/messages/${roomId}`)
      .then(res => res.json())
      .then(data => setMessages(data.result || []));

    // Subscribe WebSocket
    const token = localStorage.getItem("token");
    const socket = new SockJS(`http://localhost:8080/ws${token ? `?token=${token}` : ''}`);
    const client = new Client({
      webSocketFactory: () => socket as any,
      onConnect: () => {
        client.subscribe(`/topic/room.${roomId}`, (message) => {
          const msg: Message = JSON.parse(message.body);
          setMessages(prev => [...prev, msg]);
        });
      },
    });
    client.activate();
    stompClient.current = client;
    return () => {
      client.deactivate();
    };
  }, [roomId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = () => {
    if (!input.trim() || !stompClient.current) return;
    const msg = { roomId, content: input };
    stompClient.current.publish({
      destination: "/app/chat.sendMessage",
      body: JSON.stringify(msg),
    });
    setInput("");
  };

  return (
    <div className="fixed bottom-4 right-4 w-80 bg-white border rounded-lg shadow-lg z-50 flex flex-col">
      <div className="flex items-center justify-between p-2 border-b bg-blue-600 text-white rounded-t-lg">
        <span>Chat Room {roomId}</span>
        <button onClick={onClose} className="text-white hover:text-red-400">×</button>
      </div>
      {/* Demo: Hiển thị danh sách phòng chat của user */}
      <div className="p-2 border-b bg-gray-50 text-xs text-gray-700">
        <b>Danh sách phòng:</b>
        {loading && <div>Đang tải...</div>}
        {error && <div className="text-red-500">{error}</div>}
        <ul>
          {rooms.map((r: any) => (
            <li key={r.key.roomId}>
              {r.room?.roomId} {r.room?.name ? `- ${r.room.name}` : ''}
            </li>
          ))}
        </ul>
      </div>
      <div className="flex-1 overflow-y-auto p-2" style={{ maxHeight: 300 }}>
        {messages.map((m) => (
          <div key={m.id} className="mb-2">
            <span className="font-semibold text-blue-700">{m.sender}: </span>
            <span>{m.content}</span>
            <div className="text-xs text-gray-400">{new Date(m.sentAt).toLocaleTimeString()}</div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
      <div className="flex border-t p-2">
        <input
          className="flex-1 border rounded px-2 py-1 text-sm"
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={e => e.key === "Enter" && handleSend()}
          placeholder="Nhập tin nhắn..."
        />
        <button
          className="ml-2 bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700"
          onClick={handleSend}
        >
          Gửi
        </button>
      </div>
    </div>
  );
};

export default ChatBox;
