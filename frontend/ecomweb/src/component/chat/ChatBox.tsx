import React, { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import { fetchMessagesByRoomId, sendMessage } from "../../api/api";

interface Message {
  key: {
    messageId: string;
    roomId: string;
    sentAt: string;
  };
  sender: string;
  content: string;
  type: string;
}

interface ChatBoxProps {
  roomId: string;
  onClose: () => void;
}

const ChatBox: React.FC<ChatBoxProps> = ({ roomId, onClose }) => {
  // ...existing code...
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const stompClient = useRef<Client | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Fetch messages history qua REST API
    fetchMessagesByRoomId(roomId)
      .then(res => setMessages(res.data.result || []));

    // Subscribe WebSocket
    const token = localStorage.getItem("token");
    const socket = new SockJS(`http://localhost:8080/ws${token ? `?token=${token}` : ''}`);
    const client = new Client({
      webSocketFactory: () => socket as any,
      onConnect: () => {
        client.subscribe(`/topic/room.${roomId}`, () => {
          // Khi có tin nhắn mới, gọi lại API để lấy toàn bộ danh sách tin nhắn mới nhất
          fetchMessagesByRoomId(roomId)
            .then(res => setMessages(res.data.result || []));
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
    const userId = localStorage.getItem("userId") || "user";
    const msg = {
      key: { roomId },
      sender: userId,
      content: input,
      type: "text"
    };
  // Gửi qua WebSocket
  // stompClient.current.publish({
  //   destination: "/ws-app/chat.sendMessage",
  //   body: JSON.stringify(msg),
  // });
  // Gửi qua REST API (chỉ dùng REST để debug backend)
  sendMessage(msg);
  setInput("");
  };

  return (
  <div className="fixed bottom-4 right-4 w-80 bg-white border rounded-lg shadow-lg z-50 flex flex-col" style={{ height: 400 }}>
      <div className="flex items-center justify-between p-2 border-b bg-blue-600 text-white rounded-t-lg">
        <span>Chat Room {roomId}</span>
        <button onClick={onClose} className="text-white hover:text-red-400">×</button>
      </div>
      
      <div className="flex-1 overflow-y-auto p-2" style={{ maxHeight: 320 }}>
        {messages.map((m) => (
          <div key={m.key?.messageId || m.key?.sentAt || Math.random()} className="mb-2">
            <span className="font-semibold text-blue-700">{m.sender}: </span>
            <span>{m.content}</span>
            <div className="text-xs text-gray-400">{m.key?.sentAt ? new Date(m.key.sentAt).toLocaleTimeString() : ''}</div>
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
